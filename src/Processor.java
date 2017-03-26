
public class Processor extends Thread {
    protected final int cpuID;
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
                dsm.store("flag["+ cpuID + "]", k); //flag[cpuID] = k
                dsm.store("turn["+ k + "]", cpuID); //turn[k] = cpuID;
                
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
            dsm.store("flag[" + cpuID + "]", -1); //flag[cpuID] = -1;
        }
    }

}
