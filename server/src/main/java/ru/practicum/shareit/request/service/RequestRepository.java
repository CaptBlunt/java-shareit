package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequestorIdOrderByCreatedDateDesc(Integer userId);

    @Query(value = "select * from requests " +
            "where requestor_id <> ?1 " +
            "order by created_date", nativeQuery = true)
    List<Request> findAllNotForCreator(Integer userId, PageRequest pageable);

    @Query(value = "select * from requests " +
            "where requestor_id <> ?1 " +
            "order by created_date", nativeQuery = true)
    List<Request> findAllNotForCreator(Integer userId);
}
