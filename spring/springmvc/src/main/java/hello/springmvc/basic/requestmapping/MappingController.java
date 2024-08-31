package hello.springmvc.basic.requestmapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class MappingController {

    @RequestMapping("/hello-basic")
    public String helloBasic(){
        log.info("helloBasic");
        return "ok";
    }

    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String id){
        log.info("mappingPath userId={}",id);
        return "ok";
    }

    //특정 헤드로 매핑
    @GetMapping(value = "mapping-header", headers = "mode=debug")
    public String mappingHeader(){
        log.info("mappingHeader");
        return "ok";
    }
    //특정 미디어 타입으로 매핑
    @PostMapping(value = "mapping-header", consumes = "application/json")
    public String mappingConsumes(){
        log.info("mappingConsumes");
        return "ok";
    }

    //특정 미디어 타입으로 반환
    @PostMapping(value = "mappingProduce", produces = "text/html")
    public String mappingProduce(){
        log.info("mappingProduce");
        return "ok";
    }
}
