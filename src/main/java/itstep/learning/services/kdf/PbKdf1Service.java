package itstep.learning.services.kdf;


import itstep.learning.services.hash.HashService;
import com.google.inject.Inject;

public class PbKdf1Service implements KdfService {

    private final int iterationCount=3;
    private final int dkLength=20;
    private final HashService hashService;

    @Inject
    public PbKdf1Service(HashService hashService){


        this.hashService=hashService;
    }


    @Override
    public String dk(String password, String salt) {

        String res=password+salt;

        for(int i=0;i<iterationCount;++i){


            res=hashService.digest(res);

        }
        return res.substring(0,dkLength);
    }

}
