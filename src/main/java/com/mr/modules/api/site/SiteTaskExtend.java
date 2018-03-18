package com.mr.modules.api.site;

import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 18-3-16
 */

@Slf4j
@Component
public abstract class SiteTaskExtend extends SiteTask {

	protected RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);

	/**
	 * GET 请求
	 * @param url
	 * @return
	 */
	protected String getData(String url){
		return restTemplate.getForObject(url, String.class);
	}

	/**
	 * POST 请求
	 * @param url
	 * @param requestParams
	 * @return
	 */
	protected String postData(String url, Map<String, String> requestParams){
		HttpHeaders headers = new HttpHeaders();
		//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		//  也支持中文
		params.setAll(requestParams);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
		//  执行HTTP请求
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		//	输出结果
		return response.getBody();
	}
	/**
	 *
	 * @param targetUri
	 * @return 文件名
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	protected String downLoadFile(String targetUri) throws URISyntaxException, IOException {
		String fileName = targetUri.substring(targetUri.lastIndexOf("/") + 1);
//		String targetUri = "http://www.neeq.com.cn/uploads/1/file/public/201802/20180226182405_lc6vjyqntd.pdf";
		// 小文件
		RequestEntity requestEntity = RequestEntity.get(new URI(targetUri)).build();
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
		byte[] downloadContent = responseEntity.getBody();
		OutputStream out = new FileOutputStream(OCRUtil.DOWNLOAD_DIR + File.separator + fileName);
		out.write(downloadContent);
		out.close();
		return fileName;

		// 大文件
		//		FileOutputStream f=new FileOutputStream()
		//		ResponseExtractor<ResponseEntity<File>> responseExtractor = new ResponseExtractor<ResponseEntity<File>>() {
		//			@Override
		//			public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
		//				File rcvFile = File.createTempFile("rcvFile", "zip");
		//				FileCopyUtils.copy(response.getBody(), new FileOutputStream(rcvFile));
		//				return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(rcvFile);
		//			}
		//		};
		//		ResponseEntity<File> getFile = restTemplate.execute(targetUri, HttpMethod.GET, (RequestCallback) null, responseExtractor);

	}

	/**
	 * 提取原字符串中的中文内容
	 * @param s
	 * @return
	 */
	protected String extracterZH(String s){
		String str = s;
		String reg = "[^\u4e00-\u9fa5]";
		str = str.replaceAll(reg, "");

		return s;
	}

	/**
	 * 设置过滤字符，
	 * 用法：
	 *   List<String> keywords = new ArrayList<String>();
		 keywords.add("宋体");
		 keywords.add("黑体");
		 keywords.add("楷体");
		 keywords.add("仿宋");
		 keywords.add("普通表格");
		 keywords.add("当前位置");
		 keywords.add("首页");
		 keywords.add("政务公开");
		 keywords.add("发布时间");
		 keywords.add("页脚");
		 keywords.add("页眉");
		 keywords.add("页码");
		 keywords.add("年月日日期版权所有中国银行业监督管理委员会备号访问量次当前页访问量次");
	 * @param src
	 * @param keywords 需要过滤的字符
	 * @return
	 */
	protected String filter(String src, List<String> keywords){
		String to = src;


		for(String key : keywords){
			to = to.replace(key, "");
		}
		return to;
	}
}
