//Landon Reynolds 010975968

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class BarberShop {

    private static long time = 0;
    static int haircutTime;
    static int  numWaitChair;

    public static void main(String[] args) {

        haircutTime = Integer.valueOf(args[0]);
        int numBarberChair = Integer.valueOf(args[1]);
   
        numWaitChair = 2 * numBarberChair;
        int numCustomer = numWaitChair + numBarberChair;

        Semaphore sWait = new Semaphore(numWaitChair);
        Semaphore sBarber = new Semaphore(numBarberChair);
        Semaphore mWait = new Semaphore(1);
        Semaphore mBarber = new Semaphore(1);

        long startTime = System.currentTimeMillis();

        ArrayList<Thread> customerThreads = new ArrayList<>();

        for (int i = 1; i <= numCustomer; i++) {

            if (time < haircutTime) {

                Customer customer = new Customer(i, sWait, sBarber, mWait, mBarber);
                Thread c = new Thread(customer);

                customerThreads.add(c);
                c.start();
                customerThreads.add(c);
            }
            else {
                System.out.println("The barbershop is closed. Come again tomorrow.");
                break;
            }
            time = (int) (System.currentTimeMillis() - startTime);
        }

        for (Thread customer : customerThreads) {

            try {

                customer.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println("All customers have been given a clean cut. Closing the shop");

    }

    static class Customer implements Runnable {

        private final Random generator = new Random();
        private final int id;
        private static int numInWait = 0;
        private static int numInBarber = 0;

        private Semaphore sWait;
        private Semaphore sBarber;
        private Semaphore mWait;
        private Semaphore mBarber;

        Customer(int id, Semaphore sWait, Semaphore sBarber, Semaphore mWait, Semaphore mBarber) {

            this.id = id;
            this.sWait = sWait;
            this.sBarber = sBarber;
            this.mWait = mWait;
            this.mBarber = mBarber;

        } 

        private void getWait() {
            try {

                sWait.acquire();
                mWait.acquire();

                numInWait++;

                System.out.println("Customer " + id + " entered the waiting area. There are " + numInWait + " customers in the waiting room and " + numInBarber + " getting their hair cut.");

                mWait.release();

            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

        private void getBarberChair() {
            try {

                sBarber.acquire();
                mBarber.acquire();

                numInWait--;
                numInBarber++;

                System.out.println("Customer " + id + " enters a barber's chair. There are " + numInBarber + " customers getting their haircut and " + numInWait + " customers in the waiting room.");
                
                mBarber.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void exitShop() {
            try {

                mBarber.acquire();

                numInBarber--;

                System.out.println("Customer " + id + " finished their haircut. There are " + numInBarber + " customers getting their haircut.");

                sBarber.release();
                mBarber.release();
                sWait.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {

                Thread.sleep(generator.nextInt(1000 + 1));
                System.out.println("Customer " + id + " enters the barbershop.");

                getWait();

                if (!Thread.currentThread().isInterrupted()) {
                    
                    getBarberChair();

                    Thread.sleep(generator.nextInt(1000 + 1));

                    exitShop();
                    
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

