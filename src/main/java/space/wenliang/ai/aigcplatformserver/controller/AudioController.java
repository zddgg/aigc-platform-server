package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("audio")
public class AudioController {

    @PostMapping("playOrCreate")
    public String playOrCreate() {
        return "playOrCreate";
    }
}
