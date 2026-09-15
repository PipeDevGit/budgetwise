package com.invenio.budgetwise.budget.service;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.budget.domain.Budget;
import com.invenio.budgetwise.budget.dto.BudgetRequest;
import com.invenio.budgetwise.budget.dto.BudgetStatusResponse;
import com.invenio.budgetwise.budget.repository.BudgetRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Alertas por sobrepaso del presupuesto (issue #13).
 *
 * La regla de la alerta es excedido(), sola y sin dependencias, para probarla
 * directo. Los metodos publicos usan el mes en curso; las variantes que reciben
 * el mes existen para que las pruebas no dependan del dia en que se corren.
 */
@Service
public class BudgetService {

    private static final String CATEGORIA_INVALIDA = "La categoria indicada no existe o no te pertenece";

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Se excede cuando lo gastado SUPERA el limite. Gastar exactamente el
     * presupuesto es cumplirlo, no pasarse: ese es el caso limite de la #13.
     */
    static boolean excedido(BigDecimal limite, BigDecimal gastado) {
        return gastado.compareTo(limite) > 0;
    }

    @Transactional
    public BudgetStatusResponse definir(String email, BudgetRequest request) {
        return definir(email, request, YearMonth.now());
    }

    /**
     * Si ya habia un presupuesto para esa categoria y ese mes, se actualiza el
     * limite en vez de fallar: la tabla tiene una restriccion unica sobre
     * (usuario, categoria, mes), y cambiar de idea sobre cuanto gastar es normal.
     */
    BudgetStatusResponse definir(String email, BudgetRequest request, YearMonth mes) {
        User user = usuarioAutenticado(email);
        Category category = categoriaValida(request.categoryId(), user.getId());
        Budget budget = budgetRepository
                .findByUserIdAndCategoryIdAndPeriod(user.getId(), category.getId(), mes.toString())
                .orElseGet(() -> new Budget(request.monthlyLimit(), mes, user, category));
        budget.actualizarLimite(request.monthlyLimit());
        return aEstado(budgetRepository.save(budget), gastosDelMes(user.getId(), mes));
    }

    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> estadoDelMes(String email) {
        return estadoDelMes(email, YearMonth.now());
    }

    List<BudgetStatusResponse> estadoDelMes(String email, YearMonth mes) {
        Long userId = usuarioAutenticado(email).getId();
        List<Transaction> gastos = gastosDelMes(userId, mes);
        return budgetRepository.findByUserIdAndPeriod(userId, mes.toString()).stream()
                .map(budget -> aEstado(budget, gastos))
                .toList();
    }

    /** Solo gastos: un ingreso en la categoria Comida no descuenta del presupuesto de comida. */
    private List<Transaction> gastosDelMes(Long userId, YearMonth mes) {
        return transactionRepository.findByUserIdAndDateBetween(userId, mes.atDay(1), mes.atEndOfMonth()).stream()
                .filter(movimiento -> movimiento.getType() == TransactionType.GASTO)
                .toList();
    }

    private BudgetStatusResponse aEstado(Budget budget, List<Transaction> gastosDelMes) {
        Long categoryId = budget.getCategory().getId();
        BigDecimal gastado = gastosDelMes.stream()
                .filter(gasto -> gasto.getCategory().getId().equals(categoryId))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new BudgetStatusResponse(
                budget.getId(),
                categoryId,
                budget.getCategory().getName(),
                budget.getPeriod().toString(),
                budget.getMonthlyLimit(),
                gastado,
                excedido(budget.getMonthlyLimit(), gastado));
    }

    private User usuarioAutenticado(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    /** Mismo criterio que TransactionService: predefinida o propia, y el mismo mensaje en los dos casos. */
    private Category categoriaValida(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, CATEGORIA_INVALIDA));
        boolean accesible = category.esPredefinida() || category.getOwner().getId().equals(userId);
        if (!accesible) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CATEGORIA_INVALIDA);
        }
        return category;
    }
}
