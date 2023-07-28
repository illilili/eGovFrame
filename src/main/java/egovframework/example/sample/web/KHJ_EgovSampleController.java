/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.sample.web;

import java.util.List;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.example.sample.service.KHJ_EgovSampleService;
import egovframework.example.sample.service.KHJ_SampleDefaultVO;
import egovframework.example.sample.service.KHJ_SampleVO;

/**
 * @Class Name : EgovSampleController.java
 * @Description : EgovSample Controller Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2009.03.16           최초생성
 *
 * @author 개발프레임웍크 실행환경 개발팀
 * @since 2009. 03.16
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Controller
public class KHJ_EgovSampleController {

	/** EgovSampleService */
	@Resource(name = "sampleService")
	private KHJ_EgovSampleService sampleService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	/**
	 * 글 목록을 조회한다. (pageing)
	 * @param searchVO - 조회할 정보가 담긴 SampleDefaultVO
	 * @param model
	 * @return "KHJ_egovSampleList"
	 * @exception Exception
	 */
	@RequestMapping(value = "/KHJ_egovSampleList.do")
	public String selectSampleList(@ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO, ModelMap model) throws Exception {

		/** EgovPropertyService.sample */
		KHJ_searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		KHJ_searchVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(KHJ_searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(KHJ_searchVO.getPageUnit());
		paginationInfo.setPageSize(KHJ_searchVO.getPageSize());

		KHJ_searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		KHJ_searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		KHJ_searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> sampleList = sampleService.selectSampleList(KHJ_searchVO);
		model.addAttribute("resultList", sampleList);

		int totCnt = sampleService.selectSampleListTotCnt(KHJ_searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "KHJ_sample/KHJ_egovSampleList";
	}

	/**
	 * 글 등록 화면을 조회한다.
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "KHJ_egovSampleRegister"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addSample.do", method = RequestMethod.GET)
	public String addSampleView(@ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO, Model model) throws Exception {
		model.addAttribute("KHJ_sampleVO", new KHJ_SampleVO());
		return "KHJ_sample/KHJ_egovSampleRegister";
	}

	/**
	 * 글을 등록한다.
	 * @param sampleVO - 등록할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/KHJ_egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addSample.do", method = RequestMethod.POST)
	public String addSample(@ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO, KHJ_SampleVO KHJ_sampleVO, BindingResult bindingResult, Model model, SessionStatus status)
			throws Exception {

		// Server-Side Validation
		beanValidator.validate(KHJ_sampleVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("KHJ_sampleVO", KHJ_sampleVO);
			return "KHJ_sample/KHJ_egovSampleRegister";
		}

		sampleService.insertSample(KHJ_sampleVO);
		status.setComplete();
		return "forward:/KHJ_egovSampleList.do";
	}

	/**
	 * 글 수정화면을 조회한다.
	 * @param id - 수정할 글 id
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "KHJ_egovSampleRegister"
	 * @exception Exception
	 */
	@RequestMapping("/updateSampleView.do")
	public String updateSampleView(@RequestParam("selectedId") String id, @ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO, Model model) throws Exception {
		KHJ_SampleVO KHJ_sampleVO = new KHJ_SampleVO();
		KHJ_sampleVO.setId(id);
		// 변수명은 CoC 에 따라 sampleVO
		model.addAttribute(selectSample(KHJ_sampleVO, KHJ_searchVO));
		return "KHJ_sample/KHJ_egovSampleRegister";
	}

	/**
	 * 글을 조회한다.
	 * @param sampleVO - 조회할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return @ModelAttribute("sampleVO") - 조회한 정보
	 * @exception Exception
	 */
	public KHJ_SampleVO selectSample(KHJ_SampleVO KHJ_sampleVO, @ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO) throws Exception {
		return sampleService.selectSample(KHJ_sampleVO);
	}

	/**
	 * 글을 수정한다.
	 * @param sampleVO - 수정할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/KHJ_egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping("/updateSample.do")
	public String updateSample(@ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO, KHJ_SampleVO KHJ_sampleVO, BindingResult bindingResult, Model model, SessionStatus status)
			throws Exception {

		beanValidator.validate(KHJ_sampleVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("KHJ_sampleVO", KHJ_sampleVO);
			return "KHJ_sample/KHJ_egovSampleRegister";
		}

		sampleService.updateSample(KHJ_sampleVO);
		status.setComplete();
		return "forward:/KHJ_egovSampleList.do";
	}

	/**
	 * 글을 삭제한다.
	 * @param sampleVO - 삭제할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/KHJ_egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping("/deleteSample.do")
	public String deleteSample(KHJ_SampleVO KHJ_sampleVO, @ModelAttribute("KHJ_searchVO") KHJ_SampleDefaultVO KHJ_searchVO, SessionStatus status) throws Exception {
		sampleService.deleteSample(KHJ_sampleVO);
		status.setComplete();
		return "forward:/KHJ_egovSampleList.do";
	}

}
