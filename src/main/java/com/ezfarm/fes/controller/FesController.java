package com.ezfarm.fes.controller;

import com.ezfarm.fes.elastic.ElasticResultMap;
import com.ezfarm.fes.elastic.service.ElasticService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/fes")
public class FesController {

    @Autowired
    private ElasticService service;

    Logger logger = LoggerFactory.getLogger(FesController.class);

    @GetMapping("/search")
    public String search(Model model) throws Exception{

        ElasticResultMap result = null;

        String index = "";
        index = "/wiselake_daily_data_count";

        String qry = "";
        qry += "    {";
        qry += "        \"sort\": [";
        qry += "        {";
        qry += "            \"agg_dt\": {";
        qry += "                \"order\": \"desc\"";
        qry += "            }";
        qry += "        }";
        qry += "    ],";
        qry += "    \"size\": 1";
        qry += "}";

        result = service.fesSearch(index, qry);
        model.addAttribute("fes", result);


        return "dashboard";
    }
}
