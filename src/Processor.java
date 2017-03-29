
public class Processor extends Thread {
    protected final int cpuID;
    protected Lock storeLock;
    protected Lock loadLock;
    private DSM dsm;
    
    protected Processor(int cpuID, DSM dsm)
    {
        this.cpuID = cpuID;
        this.dsm = dsm;
        this.loadLock = new Lock();
    }
    
    @Override
    public void run()
    {
        //2. Execute algorithm
        while(true)
        {
            //<ENTRY SECTION>
            for(int k = 0; k < JavaThreads.testSize-1; k++)
            {
                //flag[cpuID] = k
                String request = "flag["+ cpuID + "]";
                store(request, k); //flag[cpuID] = k

                
                //turn[k] = cpuID;
                request = "turn["+ k + "]";
                store(request, cpuID);
                
                //Concurrency slide 66
                boolean exists = false;
                do
                {
                    exists = false;
                    for(int j = 0; j < JavaThreads.testSize; j++)
                    {
                        if(j != cpuID)
                        {
                            if((int) load("flag[" + j + "]") >= k) //flag[j] >= k
                            {
                                exists = true;
                                break;
                            }
                        }
                    }
                    
                }while(exists && ((int) load("turn[" + k + "]") == cpuID)); //exists && turn[k] == cpuID
            }
                
            //<CRITICAL SECTION>
            try
            {
                System.out.println("Processor " + cpuID + " entering the critical section!");
                Thread.sleep(5000);
                System.out.println("Processor " + cpuID + " leaving the critical section!");
            }
            catch (InterruptedException e)
            {
                
                e.printStackTrace();
            }
            
            //<EXIT SECTION>
            String request = "flag[" + cpuID + "]";
            store(request, -1);
        }
            
    }
    
    private void store(String key, Object value)
    {
        synchronized(storeLock)
        {
            try 
            {
                storeLock.setID(key);
                storeLock.wait();
                Message<String, Object> message = new Message<String, Object>(key, value);
                message.configure(OPCODE.storeBroadcastDSM, this);
                dsm.commandQueue.put(message);
                storeLock.done();
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            
        }
    }
    
    private <T> T load(String key)
    {
        synchronized(loadLock)
        {
            try 
            {
                loadLock.setID(key);
                Message<String, Object> message = new Message<String, Object>(key, null);
                message.configure(OPCODE.loadRequestDSM, this);
                dsm.commandQueue.put(message);
                loadLock.wait();
                T value = (T) loadLock.getValue();
                return value;
            } 
            catch (InterruptedException e) 
            {
                System.out.println("I am interuppted");
                e.printStackTrace();
            }
            
        }
        
        //else return null;
        System.out.print("hit null");
        return null;
    }

}
