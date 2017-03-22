package hw5;

import java.util.Random;

public class ProducerConsumer {

	public BoundedBuffer buf;
	
	public ProducerConsumer(){
		buf = new BoundedBuffer();
	}
	
	public double consume() throws InterruptedException {
		synchronized (buf) {
			while(buf.isEmpty()){
				System.out.println("Buffer Empty, Waiting...");
				buf.wait();
			}
			double consumedItem = buf.pop();
			buf.notify();
			return consumedItem;
		}
	}
	
	public void produce(double item) throws InterruptedException{
		synchronized (buf) {
			while(buf.isFull()){
				System.out.println("Buffer Full, Waiting...");
				buf.wait();
			}
			buf.push(item);
			buf.notify();
		}
	}
	
	public static void main(String[] args) {
		ProducerConsumer pc = new ProducerConsumer();
		
		Runnable Producer = new Runnable(){
			public void run(){
				for (int i = 0 ; i < 1000000 ; i++){
					Random random = new Random();
					Double bufferElement = random.nextDouble() * 100.0;
					try {
						pc.produce(bufferElement);
					}catch (Exception e){
						System.err.println("Exception thrown in Producer... " + e.toString());
					}
				}
			}
		};
		
		Runnable Consumer = new Runnable(){
			public void run(){
				for (int i = 0 ; i < 1000000 ; i++){
					try{
						double item = pc.consume();
						System.out.println("Consumer removed " + item + " from the buffer");
					}catch (Exception e){
						System.err.println("Exception thrown in Consumer... " + e.toString());
					}
				}
			}
		};
		
		try {
			Thread t_one = new Thread(Producer);
			Thread t_two = new Thread(Consumer);
			t_one.start();
			t_two.start();
		}catch (Exception e){
			System.err.println("Exception thrown while creating threads... " + e.toString());
		}
		
		System.out.println("Total Produced: " + pc.buf.totalProduced() + " :: Total Consumed: " + pc.buf.totalConsumed());
	}
}