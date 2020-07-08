package com.xiaxinyu.utils;

import com.xiaxinyu.domain.page.PageDTO;
import org.junit.Test;

public class ClassLayoutUtilsTest {

    @Test
    public void testPrint(){
        PageDTO dto = new PageDTO();
        ClassLayoutUtils.print(dto);
    }
}
