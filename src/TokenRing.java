import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenRing {
    
    private List<TokenRingAgent> tokenAgentList;
    private Token token = null;
    
    protected TokenRing(String tokenID)
    {
        tokenAgentList = Collections.synchronizedList( new ArrayList<TokenRingAgent>());
        createToken(tokenID);
        
    }
    
    protected synchronized void register(TokenRingAgent tokenRingAgent)
    {
        tokenAgentList.add(tokenRingAgent);
        tokenRingAgent.predecessor = tokenAgentList.get(tokenAgentList.size() - 2); //last index = size - 1 then -1 for the previous agent
        tokenRingAgent.successor = tokenAgentList.get(0);
    }
    
    protected void createToken(String tokenID)
    {
        token = new Token();
        token.setID(tokenID);
        tokenAgentList.get(0).tokenBucket.offer(token);
    }
    
    protected void startAgents()
    {
        for(TokenRingAgent tokenRingAgent : tokenAgentList)
        {
            tokenRingAgent.start();
        }
    }

}
