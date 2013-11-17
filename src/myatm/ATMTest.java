package myatm;

import org.junit.Test;
import org.mockito.InOrder;

import java.lang.Double;
import java.lang.Integer;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: pti08
 * Date: 14.11.13
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class ATMTest {
    private static final double DELTA = ATM.EPSILON;
    private Random random = new Random();

    private double getPositiveDouble() {
        double value = random.nextInt();
        if (value < 0.0) value = -value;
        else if (value == 0.0) value = 1.0;
        return value;
    }

    private double getNegativeDouble() {
        return -getPositiveDouble();
    }

    private int getInteger() {
        return random.nextInt();
    }

    private double getCorrectBalance() {
        double balance = getPositiveDouble();
        final double MAGIC_NUMBER = 4;
        if (balance > Double.MAX_VALUE / MAGIC_NUMBER) {
            balance /= MAGIC_NUMBER;
        } else if (balance < DELTA) {
            balance = DELTA * MAGIC_NUMBER;
        }
        return balance;
    }

    private Card generateMocCard() {
        Card card = mock(Card.class);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(anyInt())).thenReturn(true);
        return card;
    }

    private Card generateMocCard(int pin) {
        Card card = mock(Card.class);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(anyInt())).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        return card;
    }

    private Card generateMocCard(double balance) {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(balance);
        when(account.withdrow(balance)).thenReturn(balance);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(anyInt())).thenReturn(true);
        return card;
    }

    private Card generateMocCard(double balance, int pin) {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        when(account.getBalance()).thenReturn(balance);
        when(account.withdrow(balance)).thenReturn(balance);
        when(card.getAccount()).thenReturn(account);
        when(card.isBlocked()).thenReturn(false);
        when(card.checkPin(anyInt())).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        return card;
    }

    @Test
    public void testGetMoneyInATM() throws Exception {
        double money = getPositiveDouble();
        assertEquals((new ATM(money)).getMoneyInATM(), money, DELTA);
    }

    @Test
    public void testGetMoneyInEmptyATM() throws Exception {
        assertEquals((new ATM(0.0)).getMoneyInATM(), 0.0, DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMoneyATM() throws Exception {
        new ATM(getNegativeDouble());
    }

    @Test
    public void testValidateCard() throws Exception {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        int pin = getInteger();
        double balance = getPositiveDouble();
        when(card.checkPin(anyInt())).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        when(account.getBalance()).thenReturn(balance);
        when(card.getAccount()).thenReturn(account);
        ATM atm = new ATM(getPositiveDouble());
        assertTrue(atm.validateCard(card, pin));
        assertNotNull(atm.getCard());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateCardWithNegativeBalance() throws Exception {
        Account account = mock(Account.class);
        double balance = getNegativeDouble();
        Card card = mock(Card.class);
        int pin = getInteger();
        when(account.getBalance()).thenReturn(balance);
        when(card.getAccount()).thenReturn(account);
        when(card.checkPin(anyInt())).thenReturn(false);
        when(card.checkPin(pin)).thenReturn(true);
        ATM atm = new ATM(getPositiveDouble());
        atm.validateCard(card, pin);
        assertNull(atm.getCard());
    }

    @Test
    public void testValidateCardWithWrongPin() throws Exception {
        Card card = mock(Card.class);
        int pin = getInteger();
        when(card.checkPin(anyInt())).thenReturn(false);
        when(card.checkPin(~pin)).thenReturn(true);
        ATM atm = new ATM(getPositiveDouble());
        assertFalse(atm.validateCard(card, pin));
        assertNull(atm.getCard());
    }

    @Test
     public void testValidateNullCard() throws Exception {
        double money = getPositiveDouble();
        ATM atm = new ATM(money);
        assertFalse(atm.validateCard(null, getInteger()));
        assertNull(atm.getCard());
    }

    @Test
    public void testValidateBlockedCard() throws Exception {
        Card card = generateMocCard();
        when(card.isBlocked()).thenReturn(true);
        ATM atm = new ATM(getPositiveDouble());
        assertFalse(atm.validateCard(card, getInteger()));
        assertNull(atm.getCard());
    }

    @Test
    public void testCheckBalance() throws Exception {
        double balance = getPositiveDouble();
        Card card = generateMocCard(balance);
        ATM atm = new ATM(getPositiveDouble());
        atm.validateCard(card, getInteger());
        assertEquals(atm.checkBalance(), balance, DELTA);
        verify(card.getAccount(), never()).withdrow(anyDouble());
    }

    @Test
    public void testGetCash() throws Exception {
        double balance = getCorrectBalance();
        double money = balance*100;
        Card card = generateMocCard(balance);
        ATM atm = new ATM(money);
        assertTrue(atm.validateCard(card, getInteger()));
        assertTrue(atm.validateCard(card, getInteger()));
        assertEquals(atm.getCash(balance), balance, DELTA);
        verify(card.getAccount(), times(1)).withdrow(anyDouble());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNegativeCash() throws Exception {
        double balance = getCorrectBalance();
        double money = balance/3;
        Card card = generateMocCard(balance);
        ATM atm = new ATM(money);
        assertTrue(atm.validateCard(card, getInteger()));
        atm.getCash(getNegativeDouble());
    }

    @Test//(expected = IllegalArgumentException.class)
     public void testGetSmallCashBigATM() throws Exception {
        double money = Double.MAX_VALUE;
        double balance = money*1E-30;
        Card card = generateMocCard(balance);
        ATM atm = new ATM(money);
        assertTrue(atm.validateCard(card, getInteger()));
        assertEquals(atm.getCash(balance), balance, DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSmallCashBigAccount() throws Exception {
        double money = Double.MAX_VALUE;
        double balance = money*1E-30;
        double cash = balance*1E-30;
        Card card = generateMocCard(balance);
        ATM atm = new ATM(money);
        assertTrue(atm.validateCard(card, getInteger()));
        assertEquals(atm.getCash(cash), cash, DELTA);
    }

    @Test(expected = NotEnoughMoneyInAccount.class)
    public void testCashNotEnoughInAccount() throws Exception {
        double balance = getCorrectBalance();
        double money = balance*3;
        Card card = generateMocCard(balance);
        ATM atm = new ATM(money);
        assertTrue(atm.validateCard(card, getInteger()));
        atm.getCash(balance*2);
    }

    @Test(expected = NotEnoughMoneyInATM.class)
    public void testCashNotEnoughInATM() throws Exception {
        double balance = getCorrectBalance();
        double money = balance/3;
        Card card = generateMocCard(balance);
        ATM atm = new ATM(money);
        assertTrue(atm.validateCard(card, getInteger()));
        atm.getCash(balance);
    }
}
