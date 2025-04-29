package com.staccato.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JasyptConfigTest {

    @Test
    public void jasypt() {
        //given
        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("RandomPassword");
        config.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        config.setKeyObtentionIterations("210000");
        config.setPoolSize("2");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        jasypt.setConfig(config);
        String password = "staccato";

        //when
        String encryptedText = jasypt.encrypt(password);
        String decryptedText = jasypt.decrypt(encryptedText);

        //then
        assertThat(encryptedText).isNotEqualTo(password);
        assertThat(decryptedText).isEqualTo(password);
    }

}
