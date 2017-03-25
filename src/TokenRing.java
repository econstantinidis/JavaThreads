import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenRing {
    
    private List<TokenRingAgent> tokenAgentList;
    protected TokenRing()
    {
        Collections.synchronizedList( new ArrayList<TokenRingAgent>());
    }
    
    protected synchronized void register(TokenRingAgent tokenRingAgent)
    {
        tokenAgentList.add(tokenRingAgent);
    }

}
