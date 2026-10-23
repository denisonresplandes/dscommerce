package com.devsuperior.dscommerce.orders.queries.findallbyuserid;

import com.devsuperior.dscommerce.orders.domain.OrderReadRepository;
import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import com.devsuperior.dscommerce.orders.queries.OrderResponseMapper;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.api.UserModuleApi;
import com.devsuperior.dscommerce.users.application.exception.UserNotFoundException;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FindAllOrdersByUserIdHandler {

    private final OrderReadRepository repository;
    // Já usa o contrato público do módulo users (users.api), não mais o repositório interno dele.
    // TRANSITÓRIO: este handler ainda importa UserId e UserNotFoundException de users. Na Etapa 3 (módulo orders)
    // eles viram ClientId, ClientNotFoundException e uma porta ClientLookup (Anti-Corruption Layer).
    private final UserModuleApi userModuleApi;

    public FindAllOrdersByUserIdHandler(OrderReadRepository repository, UserModuleApi userModuleApi) {
        this.repository = repository;
        this.userModuleApi = userModuleApi;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> execute(FindAllOrdersByUserIdQuery query) {
        DomainException.requireNonNull(query, "user id query cannot be null");
        UserId userId = new UserId(query.userId());
        if (!userModuleApi.existsById(userId.value())) {
            throw new UserNotFoundException(userId.value());
        }
        return repository.findAllByUserId(userId)
                .stream()
                .map(OrderResponseMapper::toResponse)
                .toList();
    }
}