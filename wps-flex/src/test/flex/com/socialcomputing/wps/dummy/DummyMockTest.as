package com.socialcomputing.wps.dummy {

    import org.flexunit.rules.IMethodRule;
    import org.mockito.integrations.flexunit4.MockitoRule;
    import org.mockito.integrations.verify;

    public class DummyMockTest
    {
        [Rule]
        public var mockitoRule:IMethodRule = new MockitoRule();

        [Mock(type="com.socialcomputing.wps.dummy.DummyApp")]
        public var mockie:DummyApp;

        public function DummyMockTest()
        {
        }

        [Test]
        public function shouldVerifyMockInvocation():void
        {
            // when
            mockie.greeting("hello");
            // then
            verify().that(mockie.greeting("hello"));
        }
    }
}
