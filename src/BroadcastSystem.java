import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class BroadcastSystem extends Thread{
    private final int capacity = 1000;
    private List<BroadcastAgent> agentList;
    protected ArrayBlockingQueue<Message<String, Object>> qListen;
    
    /**
     * Implements a private synchronized list of blocking queues for
     * outgoing messages and a protected queue for incoming messages.
     * Messages can only be exchanged when the thread is running. 
     */
    protected BroadcastSystem()
    {
        agentList = Collections.synchronizedList( new ArrayList<BroadcastAgent>());
        qListen = new ArrayBlockingQueue<Message<String, Object>>(capacity);
    }
    
    
    

    protected BroadcastAgent createAgent()
    {
        BroadcastAgent agent = new BroadcastAgent(qListen); //create new agent
        agentList.add(agent); //add to synchronized list, maybe block
        return agent;
    }
    
    /**
     * This class can only process messages when the thread is running.
     * It will process the entire incoming queue and then sleep for
     * some time.
     */
    @Override
    public void run() {
        while(true)
        {
            Message<String, Object> message = qListen.poll();
            if(message != null)
            {
                switch (message.opcode)
                {
                    case storeBS:
                        store(message);
                        break;
                    case loadBS:
                        load(message);
                        break;
                   default:
                       System.out.println("Invalid opcode in BroadcastSystem");
                }
            }
            
        }
    
    }
    
    protected void store(Message<String, Object> message)
    {
        message.configure(OPCODE.storeExecuteBA, this);
        synchronized (agentList) //http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List)
        {
            for(BroadcastAgent agent : agentList)
            {
                if(agent.commandQueue.offer(message) == false)
                {
                    System.out.println(String.format("Failed to add {0} : {1} to queue because it is full", message.getKey(), message.getValue().getClass().getName()));
                }
            }
        }
    }
    
    protected void load(Message<String, Object> message)
    {
        BroadcastAgent ba = (BroadcastAgent) message.sender;
        message.configure(OPCODE.loadExecuteBA, this);
        if(ba.commandQueue.offer(message) == false)
        {
            System.out.println(String.format("Failed to add {0} : {1} to queue because it is full", message.getKey(), message.getValue().getClass().getName()));
        }
    }
    
    
    
      

}
