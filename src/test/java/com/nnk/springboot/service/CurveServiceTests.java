package com.nnk.springboot.service;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class CurveServiceTests {
    @Autowired
    private CurvePointRepository curvePointRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void curveTest() {
        CurvePoint curvePoint = new CurvePoint(2, 10D, 12D);

        // Save
        curvePoint = curvePointRepository.save(curvePoint);
        Assert.assertNotNull(curvePoint.getCurveId());
        Assert.assertEquals(curvePoint.getTerm(), 10D, 10D);

        // Update
        curvePoint.setTerm(20D);
        curvePoint = curvePointRepository.save(curvePoint);
        Assert.assertEquals(curvePoint.getTerm(), 20D, 20D);

        // Delete
        Integer id = curvePoint.getId();
        curvePointRepository.delete(curvePoint);
        Optional<CurvePoint> curveList = curvePointRepository.findById(id);
        Assert.assertFalse(curveList.isPresent());
    }
}
