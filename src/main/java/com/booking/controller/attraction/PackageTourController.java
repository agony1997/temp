package com.booking.controller.attraction;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.booking.bean.dto.attraction.AttractionDTO;
import com.booking.bean.dto.attraction.PackageTourDTO;
import com.booking.bean.pojo.attraction.Attraction;
import com.booking.bean.pojo.attraction.PackageTour;
import com.booking.bean.pojo.attraction.PackageTourAttractionId;
import com.booking.service.attraction.AttractionService;
import com.booking.service.attraction.PackageTourService;
import com.booking.utils.Result;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/management/packageTour")
public class PackageTourController {

	
    @Autowired
    private PackageTourService packageTourService;
    
    @Autowired
    private AttractionService attractionService;
    
	/**
	 * 轉到景點首頁
	 * @param model
	 */
	@GetMapping
	private String packageTour(Model model) {
		// DTO用於分頁所需數據
		PackageTourDTO packageTourDTO = new PackageTourDTO();
		Result<PageImpl<PackageTourDTO>> findPackageTourAllResult = packageTourService.findAllPackageTour(packageTourDTO);
		
		if(findPackageTourAllResult.isFailure()) {
			return "";
		}
		
		Page<PackageTourDTO> page = findPackageTourAllResult.getData();
		
		model.addAttribute("packageTourDTO", packageTourDTO);
		model.addAttribute("page", page);

		return "management-system/attraction/packagetour-list";
	}
    
	
    @GetMapping("/details")
    @ResponseBody
    public Result<PackageTourDTO> getPackageTourDetails(@RequestParam Integer packageTourId) {
        Result<PackageTourDTO> packageTourResult = packageTourService.findPackageTourById(packageTourId);

        if (packageTourResult.isFailure()) {
            return Result.failure("無法找到該套裝行程");
        }
        return packageTourResult;
    }
	
	
	/**
	 * 轉到查詢
	 * return
	 */
	@GetMapping("/select/page")
	private String sendSelectPage() {	
		return "/management-system/attraction/packagetour-select";
	}
    
    
    
	/**
	 * 轉去create-page
	 * return
	 */
	@GetMapping("/create/page")
	private String sendCreatePage(Model model) {
        PackageTourDTO packageTourDTO = new PackageTourDTO();
        Result<PageImpl<AttractionDTO>> attractionsResult = attractionService.findAttractionAll(new AttractionDTO());
        if (attractionsResult.isFailure()) {
            return ""; 
        }
        List<AttractionDTO> attractions = attractionsResult.getData().getContent();
        model.addAttribute("packageTourDTO", packageTourDTO);
        model.addAttribute("allAttractions", attractions); 
        return "management-system/attraction/packagetour-create";
	}
	
	
	
	/**
	 * 轉去edit-page
	 * @param packageTourId
	 * @param session
	 * @param model
	 */
    @GetMapping("/edit/page")
    private String sendEditPage(@RequestParam Integer packageTourId, HttpSession session, Model model) {
        session.setAttribute("packageTourId", packageTourId);
        Result<PackageTourDTO> packageTourServiceResult = packageTourService.findPackageTourById(packageTourId);
        
        if(packageTourServiceResult.isFailure()) {
            return ""; 
        }
        
        Result<PageImpl<AttractionDTO>> attractionsResult = attractionService.findAttractionAll(new AttractionDTO()); // 使用 findAttractionAll 方法
        if (attractionsResult.isFailure()) {
            return ""; 
        }
        
        List<AttractionDTO> allAttractions = attractionsResult.getData().getContent();
        model.addAttribute("packageTour", packageTourServiceResult.getData());
        model.addAttribute("allAttractions", allAttractions); // 添加景點列表到模型
        return "management-system/attraction/packagetour-edit";
    }
	
	
	@GetMapping("/select")
	private String select(@RequestParam Map<String, String> requestParameters, PackageTourDTO packageTourDTO, Model model) {
		
		Result<PageImpl<PackageTourDTO>> packageTourServiceResult = packageTourService.findPackagesTours(packageTourDTO);
		
		if(packageTourServiceResult.isFailure()) {
			return "";
		}
		
		Page<PackageTourDTO> page = packageTourServiceResult.getData();
		
		model.addAttribute("requestParameters", requestParameters);
		model.addAttribute("attractionDTO", packageTourDTO);
		model.addAttribute("page", page);

		return "/management-system/attraction/packagetour-list";	
	}
	
	
	
    /**
     * 新增套裝行程
     * @param packageTourDTO
     * @param attractionIds
     * @return
     */
    @PostMapping("/create")
    public String savePackageTour(PackageTourDTO packageTourDTO, @RequestParam List<Integer> attractionIds) {
        Result<PackageTour> savePackageTouResult = packageTourService.savePackageTour(packageTourDTO, attractionIds);
        if (savePackageTouResult.isFailure()) {
            return ""; 
        }
        return "redirect:/management/packageTour";
    }
    
    
    /**
     * 刪除套裝行程中的景點
     * @param packageTourId
     * @param attractionId
     * @return
     */
    @PostMapping("/deleteAttraction")
    public ResponseEntity<?> deleteAttractionFromPackage(@RequestParam Integer packageTourId, @RequestParam Integer attractionId) {
        Result<String> deleteAttractionFromPackageResult = packageTourService.removeAttractionFromPackage(packageTourId, attractionId);
        String message = deleteAttractionFromPackageResult.getMessage();
        if (deleteAttractionFromPackageResult.isFailure()) {
        	return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.ok(message);
    }
    
    
    /**
     * 刪除整個套裝行程
     * @param packageTourId
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePackageTour(@RequestParam Integer packageTourId) {
        Result<String> deletePackageTourResult = packageTourService.deletePackageTourById(packageTourId);
        String message = deletePackageTourResult.getMessage();
        if (deletePackageTourResult.isFailure()) {
        	return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.ok(message);
    }
    
    
    /**
     * 更新套裝行程中的景點
     * @param packageTourId
     * @param attractionId
     * @param updatedAttraction
     * @return
     */
    @PostMapping("/updateAttraction")
    public String updateAttractionInPackage(@RequestParam Integer packageTourId, @RequestParam Integer attractionId, Attraction updatedAttraction) {
        PackageTourAttractionId packageTourAttractionId = new PackageTourAttractionId(attractionId, packageTourId);
        Result<String> result = packageTourService.updateAttractionInPackage(packageTourAttractionId, updatedAttraction);
        if (result.isFailure()) {
            return ""; 
        }
        return "redirect:/management/packageTour";
    }
    
    
    
}
