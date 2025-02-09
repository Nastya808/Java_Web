package itstep.learning.services.kdf;

//RFC 2898 - PKCS #5: Password-Based Cryptography Specification Version 2.0 (ietf.org)

public interface KdfService {

    String dk(String password,String salt);

}
