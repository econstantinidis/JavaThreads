
public class Processor extends Thread {
    protected final int cpuID;
    protected Lock lock;
    private DSM dsm;
    
    protected Processor(int cpuID, DSM dsm)
    {
        this.cpuID = cpuID;
        this.dsm = dsm;
    }
    
    @Override
    public void run()
    {
        //1. start dsm
        dsm.start();
        
        //2. Execute algorithm
        while(true)
        {
            //<ENTRY SECTION>
            for(int k = 0; k <= JavaThreads.testSize-2; k++)
            {
                //flag[cpuID] = k
                synchronized(lock)
                {
                    try 
                    {
                        String request = "flag["+ cpuID + "]";
                        lock.setID(request);
                        lock.wait();
                        dsm.store(request, k); //flag[cpuID] = k
                    } 
                    catch (InterruptedException e) 
                    {
                        e.printStackTrace();
                    }
                    
                }
                
                //turn[k] = cpuID;
                synchronized(lock)
                {
                    try 
                    {
                        String request = "turn["+ k + "]";
                        lock.setID(request);
                        lock.wait();
                        dsm.store(request, cpuID); //turn[k] = cpuID;
                    } 
                    catch (InterruptedException e) 
                    {
                        e.printStackTrace();
                    }
                    
                }
                
                
                //Concurrency slide 66
                boolean exists = false;
                do
                {
                    exists = false;
                    for(int j = 0; j < JavaThreads.testSize; j++)
                    {
                        if(j != cpuID)
                        {
                            if((int)dsm.load("flag[" + j + "]") >= k) //flag[j] >= k
                            {
                                exists = true;
                            }
                        }
                    }
                    
                }while(exists && (int)dsm.load("turn[" + k + "]") == cpuID); //exists && turn[k] == cpuID
            }
                
            //<CRITICAL SECTION>
            try
            {
                System.out.println(String.format("Processor {0} entering the critical section!", cpuID));
                Thread.sleep(5000);
                System.out.println(String.format("Processor {0} leaving the critical section!", cpuID));
            }
            catch (InterruptedException e)
            {
                
                e.printStackTrace();
            }
            
            //<EXIT SECTION>
            synchronized(lock)
            {
                try 
                {
                    String request = "flag[" + cpuID + "]";
                    lock.setID(request);
                    lock.wait();
                    dsm.store(request, -1); //flag[cpuID] = -1;
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                
            }
            
        }
    }

}
