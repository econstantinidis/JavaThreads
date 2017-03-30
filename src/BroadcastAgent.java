import java.util.concurrent.ArrayBlockingQueue;

public class BroadcastAgent extends Thread {
    protected ArrayBlockingQueue<Message<String, Object>> broadcastQueue;
    protected ArrayBlockingQueue<Message<String, Object>> commandQueue;
    private DSM dsm;
    
    protected BroadcastAgent(ArrayBlockingQueue<Message<String, Object>> broadcastQueue)
    {
        this.broadcastQueue = broadcastQueue;
        this.commandQueue = new ArrayBlockingQueue<Message<String, Object>>(1000);
    }
    
    protected void setDSM(DSM dsm)
    {
        this.dsm = dsm;
    }
    
    /**
     * Retrieves the specified element at the head of the receive queue
     * or null if the queue is empty. This queue is populated by the
     * BroadcastSytem from its own queue which in turn is filled
     * from broadcasts.
     * @returns message   The element at the head of the queue.
     */
    protected Message<String, Object> receive()
    {
        return commandQueue.poll();
    }
    
    /**
     * BroadcastAgent will actively process its message queue.
     * This means that while running, this thread will poll the queue
     * and perform stores on the local memory cache.
     */
    @Override
    public void run()
    {
        while(true)
        {
            Message<String, Object> message = commandQueue.poll();
            if(message != null)
            {
                switch (message.opcode)
                {
                    case loadRequestBA:
                        loadRequest(message);
                        break;
                    case loadExecuteBA:
                        loadExecute(message);
                        break;
                    case storeBroadcastBA:
                        storeBroadcast(message);
                        break;
                    case storeExecuteBA:
                        storeExecute(message);
                        break;
                   default:
                       System.out.println("Invalid opcode in BroadcastAgent");
                }
            }
            
        }
    }
    
    protected synchronized void storeBroadcast(Message<String, Object> message)
    {
        message.configure(OPCODE.storeBS, this);
        try 
        {
            broadcastQueue.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    protected void loadRequest(Message<String, Object> message)
    {
        message.configure(OPCODE.loadBS, this);
        try 
        {
            broadcastQueue.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    protected void loadExecute(Message<String, Object> message)
    {
        message.configure(OPCODE.loadExecuteDSM, this);
        try 
        {
            dsm.commandQueue.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    protected void storeExecute(Message<String, Object> message)
    {
        message.configure(OPCODE.storeDSM, this);
        try 
        {
            dsm.commandQueue.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
