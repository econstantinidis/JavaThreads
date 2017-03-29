import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenRing {
    
    private List<TokenRingAgent> tokenAgentList;
    private Token token = null;
    
    protected TokenRing()
    {
        tokenAgentList = Collections.synchronizedList( new ArrayList<TokenRingAgent>());      
    }
    
    protected synchronized void register(TokenRingAgent tokenRingAgent)
    {
        tokenAgentList.add(tokenRingAgent);
        if(tokenAgentList.size() == 1)
        {
            tokenRingAgent.predecessor = tokenRingAgent; //Only this agent in the list
            tokenRingAgent.successor = tokenRingAgent;
        }
        else
        {
            tokenRingAgent.predecessor = tokenAgentList.get(tokenAgentList.size() - 2); //last index = size - 1 then -1 for the previous agent
            tokenRingAgent.successor = tokenAgentList.get(0);
        }
    }
    
    protected void createToken(String tokenID)
    {
        token = new Token();
        token.setID(tokenID);
        tokenAgentList.get(0).tokenBucket.offer(token);
    }

}
