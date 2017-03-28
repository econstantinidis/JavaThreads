import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenRing {
    
    private List<TokenRingAgent> tokenAgentList;
    private Token token = null;
    
    protected TokenRing(Token token)
    {
        Collections.synchronizedList( new ArrayList<TokenRingAgent>());
        
    }
    
    protected synchronized void register(TokenRingAgent tokenRingAgent)
    {
        tokenAgentList.add(tokenRingAgent);
        
        //set up tail agent
        TokenRingAgent lastAgent = tokenAgentList.get(tokenAgentList.size() - 1);
        lastAgent.setSuccessor(tokenRingAgent.agentID);
        
        //set up head agent
        TokenRingAgent headAgent = tokenAgentList.get(0);
        headAgent.setPredecessor(tokenRingAgent.agentID);
        
        //Set up this agent
        tokenRingAgent.setPredecessor(lastAgent.agentID);
        tokenRingAgent.setSuccessor(headAgent.agentID);
        
    }
    

}
