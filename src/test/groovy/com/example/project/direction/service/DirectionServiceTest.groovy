package com.example.project.direction.service


import com.example.project.api.dto.DocumentDto
import com.example.project.api.service.KakaoCategorySearchService
import com.example.project.direction.repository.DirectionRepository
import com.example.project.pharmacy.dto.PharmacyDto
import com.example.project.pharmacy.service.PharmacySearchService
import spock.lang.Specification

class DirectionServiceTest extends Specification {

    private PharmacySearchService pharmacySearchService = Mock()
    private  DirectionRepository directionRepository = Mock()

    private  KakaoCategorySearchService kakaoCategorySearchService = Mock()

    private  Base62Service base62Service = Mock()

    private DirectionService directionService = new DirectionService(pharmacySearchService, directionRepository, kakaoCategorySearchService, base62Service)

    private List<PharmacyDto> pharmacyDtoList

    def setup() {
        pharmacyDtoList = new ArrayList<>()
        pharmacyDtoList.addAll(
                PharmacyDto.builder()
                        .id(1L)
                        .pharmacyName("돌곶이온누리약국")
                        .pharmacyAddress("주소1")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build(),
                PharmacyDto.builder()
                        .id(2L)
                        .pharmacyName("호수온누리약국")
                        .pharmacyAddress("주소2")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build()
        )
    }

    def "거리별 정렬 테스트"() {
        given:
        String addressName = "서울 성북구 종암로 10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()
        when:
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyDtoList

        def resultList = directionService.buildDirectionList(documentDto)
        then:
        resultList.size() == 2
        resultList.get(0).getTargetPharmacyName() == "호수온누리약국"
        resultList.get(1).getTargetPharmacyName() == "돌곶이온누리약국"
    }

    def "반경 10km 이내 테스트"() {
        given:
        pharmacyDtoList.add(
                PharmacyDto.builder()
                        .id(3L)
                        .pharmacyName("경기약국")
                        .pharmacyAddress("주소3")
                        .latitude(37.3825107393401)
                        .longitude(127.236707811313)
                        .build())

        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        pharmacySearchService.searchPharmacyDtoList() >> pharmacyDtoList

        def results = directionService.buildDirectionList(documentDto)
        then:

        results.size() == 2
        results.get(0).targetPharmacyName == "호수온누리약국"
        results.get(1).targetPharmacyName == "돌곶이온누리약국"
    }
}