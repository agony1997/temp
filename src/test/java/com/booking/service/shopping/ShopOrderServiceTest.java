package com.booking.service.shopping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;

import com.booking.BookingApplication;
import com.booking.bean.dto.shopping.ShopOrderDTO;
import com.booking.utils.Result;

@SpringBootTest(classes = BookingApplication.class) // 確保這裡指定了正確的啟動類
public class ShopOrderServiceTest {

	@Autowired
	private ShopOrderService shopOrderService;

	@Test
	public void testSaveOrder() {
		System.out.println("測試開始");
		ShopOrderDTO shopOrderDTO = new ShopOrderDTO();
		shopOrderDTO.setOrderId(2);
		
		
		Result<PageImpl<ShopOrderDTO>> orders = shopOrderService.findOrders(shopOrderDTO);
		System.out.println("TEST===== "+orders.getData().getContent());
		System.out.println("測試結束");
		
	}
}
