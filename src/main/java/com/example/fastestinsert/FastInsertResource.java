package com.example.fastestinsert;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("insertPeople")
@RequiredArgsConstructor
public class FastInsertResource {

    private final FastInsertService fastInsertService;

    @PostMapping()
    public String getTestsResults(@RequestParam int noOfPeople) {
        return "";
    }

}