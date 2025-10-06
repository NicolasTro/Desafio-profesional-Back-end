package com.backend.transactions_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "ACCOUNTS-SERVICE")
public interface AccountsClient {


    /*** Actualiza el balance de una cuenta.
     *
     * @param accountId El ID de la cuenta a actualizar.
     * @param amount    La cantidad a sumar o restar del balance.
     * @param type      El tipo de operación: "credit" para sumar, "debit" para restar.
     */
    @PatchMapping("/{accountId}/balance")
    void updateBalance(
            @PathVariable("accountId") String accountId,
            @RequestParam("amount") Double amount,
            @RequestParam("type") String type
    );

}
