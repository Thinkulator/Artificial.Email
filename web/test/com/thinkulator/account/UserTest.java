/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hack
 */
public class UserTest {
    
    public UserTest() {
    }

    @Test
    public void testGetNewAccountDomain() {
        User instance = new User();
        instance.setEmail("eric@bootseg.com");
        assertEquals("eric.bootseg.com", instance.getNewAccountDomain());
        
        instance.setEmail("eric_kerin@bootseg.com");
        assertEquals("eric.kerin.bootseg.com", instance.getNewAccountDomain());
       
        instance.setEmail("eric_kerin+foo@bootseg.com");
        assertEquals("eric.kerin.foo.bootseg.com", instance.getNewAccountDomain());
       
        instance.setEmail("eric..kerin+foo@bootseg.com");
        assertEquals("eric.kerin.foo.bootseg.com", instance.getNewAccountDomain());
       
        instance.setEmail("eric.++_.kerin+foo@bootseg.com");
        assertEquals("eric.kerin.foo.bootseg.com", instance.getNewAccountDomain());
       
    }
    
}
