package gov.nist.hit.ds.siteManagement.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory;
import gov.nist.hit.ds.actorTransaction.ActorType;
import gov.nist.hit.ds.actorTransaction.TransactionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used with Site to define all the transaction a site can accept.
 * @author bmajur
 *
 */
public class TransactionCollection implements IsSerializable, Serializable {

    private static final long serialVersionUID = 1L;

    public List<TransactionBean> transactions = new ArrayList<TransactionBean>();
    public String collectionName;    // never really used
    boolean isRepositories = false; // a TransactionCollection is either for Repositories
    // or not

    public boolean equals(TransactionCollection tc) {
        if (tc == null)
            return false;
        return
                isRepositories == tc.isRepositories &&
                        ((collectionName == null) ? tc.collectionName == null : collectionName.equals(tc.collectionName)) &&
                        transactionsEquals(tc.transactions);
    }

    boolean transactionsEquals(List<TransactionBean> t) {
        List<TransactionBean> t2 = new ArrayList<TransactionBean>(t);
        for (TransactionBean b : transactions) {
            int index = index(t2, b);
            if (index == -1)
                return false;
            t2.remove(index);
        }
        return t2.size() == 0;
    }

    int index(List<TransactionBean> tc, TransactionBean b) {
        if (tc == null || b == null)
            return -1;
        int i=0;
        for (TransactionBean b1 : tc) {
            if (b.equals(b1))
                return i;
            i++;
        }
        return -1;
    }

    public void fixTlsEndpoints() {
        for (TransactionBean transbean : transactions) {
            if (transbean.endpoint == null || transbean.endpoint.equals(""))
                continue;
            if (transbean.isSecure) {
                if (transbean.endpoint.startsWith("http:"))
                    transbean.endpoint = transbean.endpoint.replaceFirst("http:", "https:");
            } else {
                if (transbean.endpoint.startsWith("https:"))
                    transbean.endpoint = transbean.endpoint.replaceFirst("https:", "http:");
            }
        }
    }

    public void removeEmptyNames() {
        List<TransactionBean> removable = new ArrayList<TransactionBean>();

        for (TransactionBean transbean : transactions) {
            if (transbean.name == null || transbean.name.trim().equals(""))
                removable.add(transbean);
        }
        transactions.removeAll(removable);
    }

    boolean contains(TransactionBean b) {
        for (TransactionBean tb : transactions) {
            if (tb.hasSameIndex(b))
                return true;
        }
        return false;
    }

    public void addTransaction(TransactionBean transbean) {
        transactions.add(transbean);
    }

    static List<String> asList(String[] arry) {
        List<String> l = new ArrayList<String>();

        for (int i=0; i<arry.length; i++)
            l.add(arry[i]);

        return l;
    }

    public int size() {
        return transactions.size();
    }

    public TransactionCollection() {} // For GWT

    // instead of the boolean, subtypes should be used
    public TransactionCollection(boolean isRepositories) {
        this.isRepositories = isRepositories;
    }

    // Not used
    @Deprecated
    TransactionCollection(String collectionName) {
        transactions = new ArrayList<TransactionBean>();
        this.collectionName = collectionName;
    }

    @Deprecated
    public void setName(String name) {
        collectionName = name;
    }

    public boolean hasActor(ActorType actor) {
        for (TransactionBean t : transactions) {
            if (!t.hasEndpoint())
                continue;
            try {
                if (actor.hasTransaction(t.getTransactionType()))
                    return true;
            } catch (Exception e) {}
        }
        return false;
    }

    public boolean hasTransaction(TransactionType tt) {
        for (TransactionBean t : transactions) {
            if (!t.hasEndpoint())
                continue;
            try {
                if (t.isType(tt))
                    return true;
            } catch (Exception e) {}
        }
        return false;
    }

    public TransactionBean lookup(TransactionType transType, boolean isSecure, boolean isAsync) {
        return lookup(transType.getName(), isSecure, isAsync);
    }

    public TransactionBean lookup(String name, boolean isSecure, boolean isAsync) {
        if (name == null)
            return null;
        for (TransactionBean t : transactions) {
            if (t.hasName(name) &&
                    isSecure == t.isSecure &&
                    isAsync == t.isAsync)
                return t;
        }
        return null;
    }

    public List<TransactionBean> lookupAll(String transactionName, boolean isSecure, boolean isAsync) {
        List<TransactionBean> tbs = new ArrayList<TransactionBean>();
        if (transactionName == null)
            return null;
        for (TransactionBean t : transactions) {
            if (t.hasName(transactionName) &&
                    isSecure == t.isSecure &&
                    isAsync == t.isAsync)
                tbs.add(t);
        }
        return tbs;
    }

    public String get(TransactionType name, boolean isSecure, boolean isAsync) {
        TransactionBean t = lookup(name, isSecure, isAsync);
        if (t == null)
            return null;
        return t.endpoint;
    }

    public String get(String name, boolean isSecure, boolean isAsync) {
        TransactionBean t = lookup(name, isSecure, isAsync);
        if (t == null)
            return null;
        return t.endpoint;
    }

    public void add(String transactionName, String endpoint, boolean isSecure, boolean isAsync) throws Exception {
        TransactionBean t = lookup(transactionName, isSecure, isAsync);
        if (t != null)
            return;
        if (isRepositories) {
            transactions.add(new TransactionBean(
                    transactionName,
                    TransactionBean.RepositoryType.REPOSITORY ,
                    endpoint,
                    isSecure,
                    isAsync));
        } else {
            transactions.add(new TransactionBean(
                    new ActorTransactionTypeFactory().getTransactionType(transactionName),
                    TransactionBean.RepositoryType.NONE,
                    endpoint,
                    isSecure,
                    isAsync));
        }
    }

    public String toString() {

        StringBuffer buf = new StringBuffer();

        buf.append("Collection Name: " + collectionName).append("\n");
        buf.append("Transactions:\n");
        for (TransactionBean t : transactions) {
            buf.append("\t");
            buf.append(t);
            buf.append("\n");
        }
        return buf.toString();
    }

}
