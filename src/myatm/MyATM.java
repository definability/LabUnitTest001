
package myatm;

public class MyATM {

    public static void main(String[] args) {
        double moneyInATM = 1000;
        ATM atm = new ATM(moneyInATM);
        Card card = null;

        moneyInATM=Double.MAX_VALUE;
        double amount=moneyInATM/1E25;
        double EPSILON=1E-03;
        double result1 = moneyInATM-EPSILON;
        double result2 = moneyInATM-amount;
        System.out.print( moneyInATM - amount - moneyInATM);
        try {
        atm.validateCard(card, 1234);
        atm.checkBalance();
        atm.getCash(999.99);
        } catch (Exception e) {

        }
    }
}
