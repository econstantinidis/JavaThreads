
public class Processor extends Thread {
    protected final int cpuID;
    protected Lock storeLock;
    protected Lock loadLock;
    private DSM dsm;
    private boolean noRing = false;
    
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
                int turnValue;
                //Concurrency slide 66
                boolean exists = false;
                do
                {
                    exists = false;
                    for(int j = 0; j < JavaThreads.testSize; j++)
                    {
                        if(j != cpuID)
                        {
                            //System.out.println("Flag was: " + (int) load("flag[" + j + "]"));
                            if((int) load("flag[" + j + "]") >= k) //flag[j] >= k
                            {
                                exists = true;
                                break;
                            }
                        }
                    }
                    turnValue = load("turn[" + k + "]");
                }while(exists && turnValue == cpuID); //exists && turn[k] == cpuID
                //System.out.println("CPU: " + cpuID + " || Turn: " + turnValue + " || Exists: " + exists + " || Level: " + k);
              
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
/*
        synchronized(storeLock) {

            try {
                storeLock.setID(key);
                //System.out.println("Processor " + cpuID + " has lock");
                storeLock.wait();
                Message<String, Object> message = new Message<String, Object>(key, value);
                message.configure(OPCODE.storeBroadcastDSM, this);
                dsm.commandQueue.put(message);
                storeLock.done();
                //System.out.println("Processor " + cpuID + " gives up lock");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

*/
        try
        {
        Message<String, Object> message = new Message<String, Object>(key, value);
        message.configure(OPCODE.storeBroadcastDSM, this);
        dsm.commandQueue.put(message);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
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
                if(noRing == true)
                {
                    return (T)dsm.loadRAW(key);
                }
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
