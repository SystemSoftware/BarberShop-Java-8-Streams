package core;

public class Barber implements Person{
	public int state = 0, sprite_state = 0;
	public Customer CustomerInChair;
	private String[] name_choice = {"A","B","C","D","E"};
	public String name;
	public int x,y;
	
	
	public Barber(int i){
		name = name_choice[i];
		
		x = 10;
		y = 30+(i*80);
	}
	
	public void cutHair(){
		System.out.println("[Barber "+name+"] Cutting the hair of Customer "+CustomerInChair.getName()+".");
		
		sprite_state = 0;
		if(name.equals("A")){
			x = 330;
		}else{
			x = 640;
		}
		y = 40;
	}
	
	public void acceptPayment(Customer c){
		System.out.println("[Barber "+name+"] Accepting payement from Customer "+c.getName()+".");
		
		sprite_state = 1;
		x = 64;
		y = 250;
	}

	public void sleepInChair() {
		System.out.println("[Barber "+name+"] Sleeping in chair.");
		if(name.equals("A")){
			x = 370;
		}else{
			x = 680;
		}
		y = 80;
	}
}
