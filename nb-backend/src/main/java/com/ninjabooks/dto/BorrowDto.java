package com.ninjabooks.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since  1.0
 */
public class BorrowDto implements Serializable
{
    private static final long serialVersionUID = 3445982100156009090L;

    private String borrowDate;
    private String returnDate;
    private boolean canExtendBorrow;

    public BorrowDto() {
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate.toString();
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate.toString();
    }

    public boolean getCanExtendBorrow() {
        return canExtendBorrow;
    }

    public void setCanExtendBorrow(boolean canExtendBorrow) {
        this.canExtendBorrow = canExtendBorrow;
    }
}