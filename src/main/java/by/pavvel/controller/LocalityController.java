package by.pavvel.controller;

import by.pavvel.dto.LocalityDto;
import by.pavvel.model.Locality;
import by.pavvel.service.LocalityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/localities")
public class LocalityController {

    private final LocalityService localityService;

    public LocalityController(LocalityService localityService) {
        this.localityService = localityService;
    }

    @PostMapping
    public ResponseEntity<?> addAttraction(@Valid @RequestBody Locality locality,
                                           BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        localityService.addLocality(locality);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{localityId}")
    public ResponseEntity<?> updateAttraction(@Valid @RequestBody LocalityDto localityDto,
                                              @PathVariable("localityId") Long localityId,
                                              BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        localityService.updateLocalityInfo(localityDto, localityId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
