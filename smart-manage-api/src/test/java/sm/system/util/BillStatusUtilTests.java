package sm.system.util;

import org.junit.jupiter.api.Test;
import sm.system.enums.BillStatusEnum;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BillStatusUtilTests {

    @Test
    void savedBillCanBeSubmitted() {
        assertEquals(BillStatusEnum.SUBMITTED.getValue(),
                BillStatusUtil.submit(BillStatusEnum.SAVED.getValue()));
    }

    @Test
    void submittedBillCannotBeSavedAgain() {
        BizException exception = assertThrows(BizException.class,
                () -> BillStatusUtil.requireCanSave(BillStatusEnum.SUBMITTED.getValue()));
        assertEquals(ResultEnum.BILL_STATUS_ERROR.getCode(), exception.getCode());
    }
}
