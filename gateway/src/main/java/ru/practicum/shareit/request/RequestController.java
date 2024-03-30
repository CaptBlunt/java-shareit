package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;


    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestBody @Valid CreateItemRequest request, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return requestClient.postRequest(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItemRequestResponse(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return requestClient.getUsersItemRequestResponse(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId, @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestClient.getAllRequests(userId, from ,size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@PathVariable @Min(1) Integer id, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return requestClient.getRequest(id, userId);
    }
}
