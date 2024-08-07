package com.back.takeeat.repository.Marketorder;

import com.back.takeeat.domain.order.Order;
import com.back.takeeat.dto.marketorder.request.MarketOrderSearchRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MarketOrderRepositoryImpl implements MarketOrderRepositoryCustom {

    private final EntityManager em;

    /**
     *
     "SELECT o " +
     "FROM Order o INNER JOIN FETCH o.orderMenus " +
     "WHERE o.market.id = :marketId " +
     "AND o.orderStatus = :orderStatus"
     */
    @Override
    public List<Order> findAllWithSortOrder(Long marketId, MarketOrderSearchRequest searchRequest) {

        String jpql = "select o from Order o inner join fetch o.orderMenus where o.market.id = :marketId AND o.orderStatus IN (:orderStatuses) ";

        String orderBySql = "order by ";

        if(searchRequest.getSortOrder().equals("latest")) {
            orderBySql += "o.createdTime desc";
        }else if(searchRequest.getSortOrder().equals("oldest")) {
            orderBySql += "o.createdTime asc";
        }

        jpql += orderBySql;

        return em.createQuery(jpql, Order.class)
                .setParameter("marketId", marketId)
                .setParameter("orderStatuses", searchRequest.getOrderStatuses())
                .getResultList();

    }

    @Override
    public List<Order> findAllWithSearch(Long marketId, MarketOrderSearchRequest searchRequest) {

        String jpql = "SELECT o " +
                "FROM Order o INNER JOIN FETCH o.orderMenus " +
                "WHERE o.market.id = :marketId " +
                "AND o.orderStatus IN (:orderStatuses) " +
                "AND DATE_FORMAT(o.createdTime, '%Y-%m-%d') = DATE_FORMAT(:date, '%Y-%m-%d') ";

        String orderBySql = "order by ";

        if(searchRequest.getSortOrder().equals("latest")) {
            orderBySql += "o.createdTime desc";
        }else if(searchRequest.getSortOrder().equals("oldest")) {
            orderBySql += "o.createdTime asc";
        }

        jpql += orderBySql;

        return em.createQuery(jpql, Order.class)
                .setParameter("marketId", marketId)
                .setParameter("orderStatuses", searchRequest.getOrderStatuses())
                .setParameter("date", searchRequest.getSelectDate())
                .getResultList();
    }
}
