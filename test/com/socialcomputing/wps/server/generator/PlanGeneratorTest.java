package com.socialcomputing.wps.server.generator;

import org.junit.Test;

import com.socialcomputing.utils.EZParams;
import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

public class PlanGeneratorTest {

	/**
	 * Manage the main() args.
	 */
	private static EZParams s_params;

	@Test
	public void createPlan(String[] args) {
		try {
			s_params = new EZParams(args);

			EZTimer timer = new EZTimer();
			boolean testDeg = s_params.isEnabled("degenerate"), testNet = s_params
					.isEnabled("testNet"), isVisual = s_params
					.isEnabled("visual");
			PlanParams[] params = new PlanParams[] {
					new PlanParams("charon", "Boosol",
							AnalysisProfile.PERSONAL_PLAN, 1), // 0 flugue Plan
					new PlanParams("charon", "SEngine",
							AnalysisProfile.PERSONAL_PLAN, 1), // 1 mapstan
					new PlanParams("charon", "SEngine",
							AnalysisProfile.PERSONAL_PLAN, 12), // 2 amiga ppc
					new PlanParams("charon", "SEngine",
							AnalysisProfile.DISCOVERY_PLAN, 25), // 3
																	// mapstan.net
					new PlanParams("charon", "SEngine",
							AnalysisProfile.DISCOVERY_PLAN, 1), // 4 mapstan.com
					new PlanParams("charon", "SEngine",
							AnalysisProfile.DISCOVERY_PLAN, 4), // 5 mapstan.net
					new PlanParams("charon", "SEngine",
							AnalysisProfile.DISCOVERY_PLAN, 250), // 6
																	// linternaute
					new PlanParams("charon", "SEngine",
							AnalysisProfile.PERSONAL_PLAN, 31206),// 7 Amiga PPC
					new PlanParams("charon", "SEngine",
							AnalysisProfile.PERSONAL_PLAN, 59863),// 8 mapstan
																	// search
					new PlanParams("charon", "Boosol",
							AnalysisProfile.PERSONAL_PLAN, 2), // 9 espinat Plan
					new PlanParams("charon", "Boosol",
							AnalysisProfile.GLOBAL_PLAN, 6227), // 10
					new PlanParams("charon", "SEngine",
							AnalysisProfile.PERSONAL_PLAN, 27), // 11 bug
			};

			PlanParams planPrm = params[11];
			ProtoPlan protoPlan;
			PlanGenerator planGenerator = new PlanGenerator();

			// Test degenerated cases
			if (testDeg) {
				protoPlan = planGenerator.preGenerate(planPrm);
				protoPlan.degenerate();
			} else {
				iEnumerator<String> enumerator = testNet ? getNetEnumerator() : null;

				do {
					timer.reset();

					if (testNet) {
						String item = enumerator.next();
						planPrm = new PlanParams("charon", "SEngine",
								AnalysisProfile.PERSONAL_PLAN,
								Integer.parseInt(item));
					}

					protoPlan = planGenerator.preGenerate(planPrm);
					timer.showElapsedTime("init " + planPrm.getPlanName());

					System.out
							.println("\n--==< PLAN GENERATION STARTED >==--\n");
					timer.reset();

					planGenerator = new PlanGenerator();

					planGenerator.generatePlan(protoPlan, isVisual);
					timer.showElapsedTime("Plan generated");

					planGenerator.postGenerate(planPrm);// getIntValue(
														// "output", SERIALZ_OUT
														// ));

					System.out
							.println("\n--==< PLAN GENERATION FINISHED >==--\n");
				} while (testNet && enumerator.hasNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static iEnumerator<String> getNetEnumerator()
			throws com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException {
		WPSDictionary dico = WPSDictionary.CreateTestInstance("Boosol");
		dico.openConnections(null);
		return dico.getEntityConnector().getEnumerator();
	}

}
