/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myatm;

public class ATM {
        
    //Можно задавать количество денег в банкомате 
    ATM(double moneyInATM){
         throw new UnsupportedOperationException("Not yet implemented");
    }

    public double getMoneyInATM() {
         throw new UnsupportedOperationException("Not yet implemented");
    }
        
    //С вызова данного метода начинается работа с картой
    //Метод принимает карту и пин-код, проверяет пин-код карты и не заблокирована ли она
    //Если неправильный пин-код или карточка заблокирована, возвращаем false. При этом, вызов всех последующих методов у ATM с данной картой должен генерировать исключение NoCardInserted
    public boolean validateCard(Card card, int pinCode){
         throw new UnsupportedOperationException("Not yet implemented");        
    }
    
    //Возвращает сколько денег есть на счету
    public double checkBalance(){
         throw new UnsupportedOperationException("Not yet implemented");
    }
    
    //Метод для снятия указанной суммы
    //Метод возвращает сумму, которая у клиента осталась на счету после снятия
    //Кроме проверки счета, метод так же должен проверять достаточно ли денег в самом банкомате
    //Если недостаточно денег на счете, то должно генерироваться исключение NotEnoughMoneyInAccount 
    //Если недостаточно денег в банкомате, то должно генерироваться исключение NotEnoughMoneyInATM 
    //При успешном снятии денег, указанная сумма должна списываться со счета, и в банкомате должно уменьшаться количество денег
    public double getCash(double amount){
         throw new UnsupportedOperationException("Not yet implemented");
    }
}
