package com.socialcomputing.wps.dummy {

    import flexunit.framework.Assert;
    import flash.errors.IOError;

    public class DummyTest {

        [Test]
        public function addition():void {
            Assert.assertEquals(12, 7 + 5);
        }
        
        [Test(expects="flash.errors.IOError")]
        public function doIOError():void {
            // A test which causes an IOError
            throw new IOError();
        }
    }
}
