package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED, ALL, CURRENT, FUTURE, PAST;


    public static BookingStatus from(String status) {
        for (BookingStatus value : BookingStatus.values()) {
            if (value.name().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
