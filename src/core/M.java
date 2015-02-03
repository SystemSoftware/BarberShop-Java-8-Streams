package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class M {
	public static int CUSTOMERS_IN_SHOP = 3;
	public static int DELAY_BETWEEN_MOVES = 100; // in ms
	public static int CUSTOMERS = 5;
	public static int SOFA_SEATS = 2;
	public static int BARBERS = 2;
	public static int cash;
	public static List<Person> p;
	public static List<Person> shop;
	public static Semaphore shop_sema, sofa_sema, register_sema, sofa_pos;
	public static ConcurrentLinkedQueue<Customer> sofa;
	public static boolean firstOnSofa = true;

	public static void main(String[] args) {
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "6");
		shop_sema = new Semaphore(CUSTOMERS_IN_SHOP);
		sofa_sema = new Semaphore(SOFA_SEATS);
		register_sema = new Semaphore(1);
		sofa_pos = new Semaphore(1);
		
		Visualisation v = new Visualisation();
		List<Person> tmp = new ArrayList<Person>();
		for(int i=0; i < CUSTOMERS+BARBERS; i++){
			if(i<CUSTOMERS){
				tmp.add( new Customer( String.valueOf(i),i ) );
			}else{
				tmp.add( new Barber((CUSTOMERS+BARBERS)-i-1) );
			}
		}
		
		shop = new ArrayList<Person>();
		
		sofa = new ConcurrentLinkedQueue<Customer>();
		p = Collections.synchronizedList(tmp);
		
		p
			.parallelStream()
			.forEach(i -> Barbershop(i, v) );
		
		
	}
	

	public static void Barbershop(Person p, Visualisation v){
		boolean isCustomer = p instanceof Customer;
		Customer c = null;
		Barber b = null;
		if(isCustomer){
			c = (Customer) p;
		}else{
			b = (Barber) p;
		}
		
		while(true){
			if(isCustomer){ // Customer
				if(!c.isInShop() ){
					// so is not in shop, lets try to enter it
					try {
						shop_sema.acquire();
						c.enterShop();
					} catch (InterruptedException e) {e.printStackTrace(); }
				}else{
					// is in shop, lets try to sit on the sofa
					if(!c.isOnSofa() && !c.served){
						try {
							c.x = 550;
							c.y = 220;
							sofa_sema.acquire();
							c.sitOnSofa();
						} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
				try {Thread.sleep(DELAY_BETWEEN_MOVES);} catch (InterruptedException e) {}
			}else{ // Barber
				switch(b.state){ // what to do next?
					case 0: // is customer on the sofa?
						if(!sofa.isEmpty()){
							Customer c_free = (Customer)sofa.poll();
							if(c_free != null){
								sofa_sema.release();
								c_free.sitInBarberChair(b);
								b.state++;
							}
						}
					break;
					case 1: // cut the hair
						b.cutHair();
						b.state++;
					break;
					case 2: // try to get the register and let the customer pay
						try {
							register_sema.acquire();
							// got to the register now let the customer pay
							Customer c2 = b.CustomerInChair;
							c2.pay(b);
							cash+=13;
							c2.exitShop();
						} catch (InterruptedException e) {
						} finally{
							register_sema.release();
						}
						b.state++;
						break;
					case 3: // sleep in chair
						b.sleepInChair();
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {e.printStackTrace();}
						b.state = 0;
					break;
						
				}

			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
			v.update();
		}
		
	}
}
