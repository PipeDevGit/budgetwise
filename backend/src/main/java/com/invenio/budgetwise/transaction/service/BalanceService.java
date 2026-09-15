package com.invenio.budgetwise.transaction.service;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.dto.BalanceResponse;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Calculo automatico del saldo (issue #15): ingresos totales, gastos totales
 * y la diferencia entre los dos.
 *
 * El calculo esta separado de la consulta a proposito. calcular() recibe la
 * lista de movimientos y no toca la base ni Spring, asi que se prueba con
 * datos armados a mano en BalanceServiceTest. Es lo que pide CLAUDE.md para
 * la logica de la que dependen los puntos de pruebas.
 */
@Service
public class BalanceService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public BalanceService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public BalanceResponse saldoDe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        // Es la misma consulta del listado de la #7, con el desempate por id
        // de la #15: ya filtra por usuario, asi que el saldo nunca puede
        // incluir movimientos de otra persona.
        return calcular(transactionRepository.findByUserIdOrderByDateDescIdDesc(user.getId()));
    }

    /**
     * BigDecimal en toda la cuenta: con double, 0.10 + 0.20 da
     * 0.30000000000000004, y un saldo con ese error no se puede mostrar.
     */
    BalanceResponse calcular(List<Transaction> movimientos) {
        BigDecimal ingresos = sumar(movimientos, TransactionType.INGRESO);
        BigDecimal gastos = sumar(movimientos, TransactionType.GASTO);
        return new BalanceResponse(ingresos, gastos, ingresos.subtract(gastos));
    }

    private BigDecimal sumar(List<Transaction> movimientos, TransactionType tipo) {
        return movimientos.stream()
                .filter(movimiento -> movimiento.getType() == tipo)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
