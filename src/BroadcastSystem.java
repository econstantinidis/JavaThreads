import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class BroadcastSystem extends Thread{
    private final int sleepSeconds = 5;
    private final int capacity = 1000;
    private List<ArrayBlockingQueue<Message<String, Object>>> qList;
    protected ArrayBlockingQueue<Message<String, Object>> qListen;
    
    /**
     * Implements a private synchronized list of blocking queues for
     * outgoing messages and a protected queue for incoming messages.
     * Messages can only be exchanged when the thread is running. 
     */
    protected BroadcastSystem()
    {
        qList = Collections.synchronizedList( new ArrayList<ArrayBlockingQueue<Message<String, Object>>>());
        qListen = new ArrayBlockingQueue<Message<String, Object>>(capacity * 5);
    }
    
    /**
     * Multiple agents are prevented from accessing the list of blocking queues.
     * Offering an message inserts it into the tail of all queues, if the queue is full then
     * an error message is printed to the console but execution continues 
     * @param message     The message to be inserted into every queue
     */
    private void offerMessage(Message<String, Object> message)
    {
        synchronized (qList) //http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List)
        {
            for(ArrayBlockingQueue<Message<String, Object>> queue : qList)
            {
                if(queue.offer(message) == false)
                {
                    System.out.println(String.format("Failed to add {0} : {1} to broadcast queue because it is full", message.getKey(), message.getValue().getClass().getName()));
                }
            }
        }
    }
    
    /**
     * Thread safe, create and register a message queue and
     * add it to the synchronized list of outgoing queues.
     * Only after the queue has successfully been added will
     * an agent be generated and returned.
     * @param start     Determines if the agent thread starts immediately
     * @return          The reference to a new agent
     */
    protected BroadcastAgent createAgent(boolean start)
    {
        ArrayBlockingQueue<Message<String, Object>> qBroadcast = new ArrayBlockingQueue<>(capacity); //generate new blocking queue
        qList.add(qBroadcast); //add to synchronized list, maybe block
        BroadcastAgent agent = new BroadcastAgent(qBroadcast, qListen); //create new agent
        if(start)
        {
            agent.start();
        }
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
            //Process the queue
            while(qListen.isEmpty() == false)
            {
                Message<String, Object> message = qListen.poll();
                if(message != null)
                {
                    offerMessage(message);
                }
            }
            
            //Sleep for some time
            try 
            {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
    
    
    
      

}
