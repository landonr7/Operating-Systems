//Landon Reynolds 010975968

import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;

class Customer implements Runnable {
    private int id;
    private Semaphore sWait;
    private Semaphore mWait;
    private Semaphore sProduce;
    private Semaphore mProduce;
    private Semaphore sGeneral;
    private Semaphore mGeneral;
    private Semaphore sFrozen;
    private Semaphore mFrozen;
    private Semaphore sCashier;
    private Semaphore mCashier;
    private int timeUnits;

    public Customer(int id, Semaphore sWait, Semaphore mWait, Semaphore sProduce, Semaphore mProduce,
                    Semaphore sGeneral, Semaphore mGeneral, Semaphore sFrozen, Semaphore mFrozen,
                    Semaphore sCashier, Semaphore mCashier, int timeUnits) {
        this.id = id;
        this.sWait = sWait;
        this.mWait = mWait;
        this.sProduce = sProduce;
        this.mProduce = mProduce;
        this.sGeneral = sGeneral;
        this.mGeneral = mGeneral;
        this.sFrozen = sFrozen;
        this.mFrozen = mFrozen;
        this.sCashier = sCashier;
        this.mCashier = mCashier;
        this.timeUnits = timeUnits;
    }

        private void getWaitingRoom() {
        try {

            mWait.acquire();
            sWait.acquire();
            System.out.println("Customer " + id + " enters the waiting room.");
            mWait.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void getProduceSection() {
        try {

            mProduce.acquire();
            sProduce.acquire();
            System.out.println("    Customer " + id + " enters Produce Section. Current customers in Produce Section: " + (20 - sProduce.availablePermits()));
            mProduce.release();

            Thread.sleep((new Random().nextInt(timeUnits)) * 100);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void getGeneralSection() {
        try {
  
            mGeneral.acquire();
            sGeneral.acquire();
            System.out.println("        Customer " + id + " enters General Grocery Section. Current customers in General Grocery Section: " + (25 - sGeneral.availablePermits()));
            mGeneral.release();

            Thread.sleep((new Random().nextInt(timeUnits)) * 150);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void getFrozenSection() {
        try {
            
            mFrozen.acquire();
            sFrozen.acquire();
            System.out.println("            Customer " + id + " enters Frozen Section. Current customers in Frozen Section: " + (30 - sFrozen.availablePermits()));
            mFrozen.release();

            Thread.sleep((new Random().nextInt(timeUnits)) * 300);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void getCashierSection() {
        try {
 
            mCashier.acquire();
            sCashier.acquire();
            System.out.println("                Customer " + id + " enters Cashier Section. Current customers in Cashier Section: " + (10 - sCashier.availablePermits()));
            mCashier.release();

            Thread.sleep((new Random().nextInt(timeUnits)) * 100);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void exitGroceryStore() {

        System.out.println("                        Customer " + id + " exits the grocery store.");
        sWait.release();
        sProduce.release();
        sGeneral.release();
        sFrozen.release();
        sCashier.release();
    }


    @Override
    public void run() {
        System.out.println("Customer " + id + " enters the grocery store.");

        getWaitingRoom();

        getProduceSection();

        getGeneralSection();

        getFrozenSection();

        getCashierSection();

        exitGroceryStore();
    }
}

public class GroceryStore {
    public static void main(String[] args) {
        if (args.length != 2) {
            return;
        }

        int timeUnits = Integer.parseInt(args[0]);
        int numCustomers = Integer.parseInt(args[1]);

        Semaphore sWait = new Semaphore(40);
        Semaphore mWait = new Semaphore(1);
        Semaphore sProduce = new Semaphore(20);
        Semaphore mProduce = new Semaphore(1);
        Semaphore sGeneral = new Semaphore(25);
        Semaphore mGeneral = new Semaphore(1);
        Semaphore sFrozen = new Semaphore(30);
        Semaphore mFrozen = new Semaphore(1);
        Semaphore sCashier = new Semaphore(10);
        Semaphore mCashier = new Semaphore(1);

        ArrayList<Thread> customerThreads = new ArrayList<>();

        for (int i = 1; i <= numCustomers; i++) {
            customerThreads.add(new Thread(new Customer(i, sWait, mWait, sProduce, mProduce, sGeneral, mGeneral,
                    sFrozen, mFrozen, sCashier, mCashier, timeUnits)));
        }

        for (Thread customerThread : customerThreads) {
            customerThread.start();
        }

        try {
            Thread.sleep(timeUnits * 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

