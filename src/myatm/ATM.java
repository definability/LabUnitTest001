/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myatm;

import java.math.BigDecimal;

public class ATM {

    public static final double EPSILON = 1E-03;

    public Card getCard() {
        return card;
    }

    private Card card;
    private BigDecimal moneyInATM;

    //Можно задавать количество денег в банкомате
    public ATM(double moneyInATM) {
        init(moneyInATM);
    }

    private void init(double moneyInATM) {
        setMoneyInATM(moneyInATM);
    }

    public void setMoneyInATM(double moneyInATM) {
        if (moneyInATM < 0.0) {
            // :D
            // moneyInATM = -moneyInATM;
            // Let's be serious
            throw new IllegalArgumentException("You can't set negative money amount");
        } else {
            this.moneyInATM = new BigDecimal(moneyInATM);
        }
    }

    public double getMoneyInATM() {
        return this.moneyInATM.doubleValue();
    }

    //С вызова данного метода начинается работа с картой
    //Метод принимает карту и пин-код, проверяет пин-код карты и не заблокирована ли она
    //Если неправильный пин-код или карточка заблокирована, возвращаем false. При этом, вызов всех последующих методов у ATM с данной картой должен генерировать исключение NoCardInserted
    public boolean validateCard(Card card, int pinCode) throws NoCardInserted {
        // Checking card==null must be the first
        if (card == null || card.isBlocked() || !card.checkPin(pinCode)) {
            this.card = null;
            return false;
        } else {
            this.card = card;
            if (this.card.getAccount().getBalance()<0.0) {
                throw new IllegalArgumentException("Card balance can not be negative");
            } else {
                return true;
            }
        }
    }

    //Возвращает сколько денег есть на счету
    public double checkBalance() throws NoCardInserted {
        if (this.card == null) {
            throw new NoCardInserted();
        } else {
            return this.card.getAccount().getBalance();
        }
    }

    //Метод для снятия указанной суммы
    //Метод возвращает сумму, которая у клиента осталась на счету после снятия
    //Кроме проверки счета, метод так же должен проверять достаточно ли денег в самом банкомате
    //Если недостаточно денег на счете, то должно генерироваться исключение NotEnoughMoneyInAccount 
    //Если недостаточно денег в банкомате, то должно генерироваться исключение NotEnoughMoneyInATM 
    //При успешном снятии денег, указанная сумма должна списываться со счета, и в банкомате должно уменьшаться количество денег

    /**
     * @param amount Money amount to withdraw
     * @return Withdrawn money amount
     * @throws NoCardInserted          checkBalance can throw this exception if the card was not inserted. This means that no additional checking is needed.
     * @throws NotEnoughMoneyInAccount
     * @throws NotEnoughMoneyInATM
     */
    public double getCash(double amount) throws NoCardInserted, NotEnoughMoneyInAccount, NotEnoughMoneyInATM {
        if (amount<=-EPSILON) {
            throw new IllegalArgumentException("You cannot withdraw negative number");
        } else if (Math.abs(amount) < EPSILON) {
            if (card == null) {
                throw new NoCardInserted();
            } else {
                return 0.0;
            }
        } else if (checkBalance() < amount) {
            throw new NotEnoughMoneyInAccount();
        } else if (getMoneyInATM() < amount) {
            throw new NotEnoughMoneyInATM();
        } else if (!isSubtractionWithoutLoss(moneyInATM, new BigDecimal(amount))
                || !isSubtractionWithoutLoss(checkBalance(), amount)) {
            // The problem is: WE USE DOUBLE!
            throw new IllegalArgumentException("Withdrawal amount ("+amount+") is small.");
        } else {
            amount = this.card.getAccount().withdrow(amount);
            moneyInATM = moneyInATM.subtract(new BigDecimal(amount));
            //this.moneyInATM -= amount;
            return amount;
        }
    }

    private boolean isSubtractionWithoutLoss(double n, double m) {
        return isSubtractionWithoutLoss(n, m, EPSILON);
    }


    private boolean isSubtractionWithoutLoss(BigDecimal n, BigDecimal m) {
        return isSubtractionWithoutLoss(n, m, new BigDecimal(EPSILON));
    }


    private boolean isSubtractionWithoutLoss(double n, double m, double epsilon) {
        if (n < 0 || m < 0 || n < m) {
            throw new IllegalArgumentException();
        } else {
            // WARNING: DO NOT USE OPTIMIZATION HERE!
            double r = n - m;
            return Math.abs(r - n + m) < epsilon;
        }
    }


    private boolean isSubtractionWithoutLoss(BigDecimal n, BigDecimal m, BigDecimal epsilon) {
        if (n.doubleValue() < 0 || m.doubleValue() < 0 || n.compareTo(m) == -1) {
            throw new IllegalArgumentException();
        } else {
            // WARNING: DO NOT USE OPTIMIZATION HERE!
            BigDecimal r = n.subtract(m);
            return r.subtract(n).add(m).abs().compareTo(epsilon) == -1;
        }
    }

}
