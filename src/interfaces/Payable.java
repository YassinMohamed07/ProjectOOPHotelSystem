package interfaces;

public interface Payable {
    double calculateTotal();
    boolean processPayment(double amountPaid);
}
