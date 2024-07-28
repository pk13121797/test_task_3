package by.pavvel.controller;

import by.pavvel.dto.AttractionDto;
import by.pavvel.dto.AttractionLocalityDto;
import by.pavvel.dto.Recommendation;
import by.pavvel.model.Attraction;
import by.pavvel.service.AttractionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/attractions")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public List<AttractionDto> getSortedAttractions(@RequestParam("sortDirection") String direction, @RequestParam("type") String type) {
        return attractionService.getAttractions(direction, type);
    }

    @GetMapping("/locality/{localityTitle}")
    public List<AttractionLocalityDto> getAttractionsByLocality(@PathVariable("localityTitle") String localityTitle) {
        return attractionService.getAttractionsByLocality(localityTitle);
    }

    @GetMapping("/recommendation")
    public Recommendation getAttractionWeatherInfo(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude) {
        return attractionService.getWeatherInfo(latitude, longitude);
    }

    @PostMapping
    public ResponseEntity<?> addAttraction(@Valid @RequestBody Attraction attraction,
                                           @RequestParam(value = "locality") Long localityId,
                                           @RequestParam(value = "services") List<Long> servicesIds,
                                           BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        attractionService.addAttraction(attraction, localityId, servicesIds);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{attractionId}")
    public ResponseEntity<?> updateAttraction(@PathVariable("attractionId") Long attractionId,
                                              @RequestParam(value = "abbreviation") String serviceAbbreviation) {
        attractionService.updateAttractionServicesAbbreviation(serviceAbbreviation, attractionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{attractionId}")
    public ResponseEntity<?> deleteAttraction(@PathVariable("attractionId") Long attractionId) {
        attractionService.deleteAttraction(attractionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
