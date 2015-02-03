package core;

public class Customer implements Person{
	private String name;
	public boolean InShop = false, OnSofa = false, served = false;
	public int x,y;
	private int num;
	public int state = 0;
	public boolean first = false;

	public Customer(String name, int num){
		this.name = name;
		this.num = num;
		x=10+(70*num);
		y=510;
	}
	
	public String getName(){
		return name;
	}
	/*
	enterShop,
	sitOnSofa,
	sitInBarberChair,
	pay,
	exitShop
	*/
	
	public void enterShop(){
		M.shop.add(this);
		InShop = true;
		System.out.println("[Customer "+name+"] Entered the shop.");
		
	}
	public void sitOnSofa(){
		M.sofa.add(this);
		OnSofa = true;
		
		System.out.println("[Customer "+name+"] Sit down on sofa.");
		state = 1;
		// 615, 680
		try {
			M.sofa_pos.acquire();
			if(M.firstOnSofa){
				x = 615;
				first = true;
				M.firstOnSofa = false;
			}else{
				x = 680;
			}
			/*
			if(M.sofa.peek() == this){
				
			}else{
				x = 680;
			}*/
		} catch (InterruptedException e) {
		} finally{
			M.sofa_pos.release();
		}
		
		y = 220;
	}
	public void sitInBarberChair(Barber b){
		b.CustomerInChair = this;
		OnSofa = false;
		M.sofa.remove(this);
		served = true;
		System.out.println("[Customer "+name+"] Leave sofa and sit down in barberchair.");
		
		state = 2;
		if(first){
			first = false;
			M.firstOnSofa = true;
		}
		// middle x 370, y 40,  // right x 678, y 40
		if(b.name.equals("A")){
			x = 370;
		}else{
			x = 678;
		}
		y = 40;
	}
	public void pay(Barber b){
		b.CustomerInChair = null;
		System.out.println("[Customer "+name+"] Paying £13 for the haircut to Barber "+b.name+".");
		b.acceptPayment(this);
		state = 3;
		x = 64;
		y = 180;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {e.printStackTrace();}
		
	}
	public void exitShop(){
		M.shop.remove(this);
		M.shop_sema.release();
		InShop = false;
		served = false;
		System.out.println("[Customer "+name+"] Leaving the shop.");
		
		state = 0;
		x=10+(70*num);
		y=510;
	}
	public boolean isInShop() {
		return InShop;
	}
	public boolean isOnSofa() {
		return OnSofa;
	}
	public void setInShop(boolean inShop) {
		InShop = inShop;
	}
}
