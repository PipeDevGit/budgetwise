package com.invenio.budgetwise.transaction.service;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.dto.TransactionRequest;
import com.invenio.budgetwise.transaction.dto.TransactionResponse;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * CRUD de movimientos (issue #7). Todo pasa por el email del usuario
 * autenticado (lo deja JwtAuthenticationFilter en el Authentication): ninguna
 * consulta ni escritura ocurre sin saber de quien es, tal como pide la
 * issue #7 y revisa CLAUDE.md en cada PR.
 *
 * Los metodos de lectura son @Transactional(readOnly = true) porque
 * Transaction.category es LAZY: sin una transaccion abierta durante el
 * mapeo a TransactionResponse, pedir category.getName() explotaria con un
 * LazyInitializationException fuera de la consulta original.
 */
@Service
public class TransactionService {

    private static final String TRANSACCION_INEXISTENTE = "La transaccion no existe";
    private static final String CATEGORIA_INVALIDA = "La categoria indicada no existe o no te pertenece";

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> listar(String email) {
        Long userId = usuarioAutenticado(email).getId();
        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse obtener(String email, Long id) {
        Long userId = usuarioAutenticado(email).getId();
        return aRespuesta(buscarPropia(id, userId));
    }

    @Transactional
    public TransactionResponse crear(String email, TransactionRequest request) {
        User user = usuarioAutenticado(email);
        Category category = categoriaValida(request.categoryId(), user.getId());
        Transaction transaction = new Transaction(
                request.amount(), request.type(), request.date(), request.description(), user, category);
        return aRespuesta(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse actualizar(String email, Long id, TransactionRequest request) {
        Long userId = usuarioAutenticado(email).getId();
        Transaction transaction = buscarPropia(id, userId);
        Category category = categoriaValida(request.categoryId(), userId);
        transaction.actualizar(request.amount(), request.type(), request.date(), request.description(), category);
        return aRespuesta(transaction);
    }

    @Transactional
    public void eliminar(String email, Long id) {
        Long userId = usuarioAutenticado(email).getId();
        transactionRepository.delete(buscarPropia(id, userId));
    }

    private User usuarioAutenticado(String email) {
        // JwtAuthenticationFilter ya confirmo que el email existe antes de
        // dejar pasar la peticion; esto no deberia fallar nunca en la
        // practica, pero no cuesta nada dejarlo explicito en vez de asumirlo.
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private Transaction buscarPropia(Long id, Long userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, TRANSACCION_INEXISTENTE));
    }

    private Category categoriaValida(Long categoryId, Long userId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, CATEGORIA_INVALIDA));
        boolean accesible = category.esPredefinida() || category.getOwner().getId().equals(userId);
        if (!accesible) {
            // Mismo mensaje que "no existe": no hay que confirmarle a nadie
            // que una categoria ajena existe (mismo criterio que ya se usa
            // en AuthService para no distinguir email inexistente de clave
            // incorrecta).
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CATEGORIA_INVALIDA);
        }
        return category;
    }

    private TransactionResponse aRespuesta(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName());
    }
}
