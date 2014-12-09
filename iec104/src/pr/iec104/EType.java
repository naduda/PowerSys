package pr.iec104;

public enum EType {
	M_SP_NA(0x01),
	M_SP_TA(0x02),
	M_DP_NA(0x03),
	M_DP_TA(0x04),
	M_ST_NA(0x05),
	M_ST_TA(0x06),
	M_BO_NA(0x07),
	M_BO_TA(0x08),
	M_ME_NA(0x09),
	M_ME_TA(0x0A),
	M_ME_NB(0x0B),
	M_ME_TB(0x0C),
	M_ME_NC(0x0D),
	л_ле_ря(0x0E),
	M_IT_NA(0x0F),
	M_IT_TA(0x10),
	M_EP_TA(0x11),
	л_еп_рб(0x12),
	л_еп_ря(0x13),
	M_PS_NA(0x14),
	M_ME_ND(0x15),
	M_SP_TB(0x1E),
	M_DP_TB(0x1F),
	M_ST_TB(0x20),
	M_BO_TB(0x21),
	M_ME_TD(0x22),
	л_ле_ре(0x23),
	M_ME_TF(0x24),
	M_IT_TB(0x25),
	M_EP_TD(0x26),
	л_еп_ре(0x27),
	M_EP_TF(0x28),
	
	C_SC_NA(0x2D),
	C_DC_NA(0x2E),
	C_RC_NA(0x2F),
	C_SE_NA(0x30),
	C_SE_NB(0x31),
	C_SE_NC(0x32),
	C_BO_NA(0x33),
	M_EI_NA(0x46),
	C_IC_NA(0x64),
	C_CI_NA(0x65),
	C_RD_NA(0x66),
	C_CS_NA(0x67),
	C_TS_NA(0x68),
	C_RP_NA(0x69),
	C_CD_NA(0x6A),
	
	P_ME_NA(0x6E),
	P_ME_NB(0x6F),
	P_ME_NC(0x70),
	P_AC_NA(0x71),
	
	F_FR_NA(0x78),
	F_SR_NA(0x79),
	F_SC_NA(0x7A),
	F_LS_NA(0x7B),
	F_AF_NA(0x7C),
	F_SG_NA(0x7D),
	F_DR_TA(0x7E),
	
	M_BO_TC(0x88),
	M_ME_NE(0x8B),
	M_ME_TI(0x90),
	M_ME_TJ(0x91),
	
	л_яб_рю(0x96),
	л_яб_рб(0x97),
	л_яб_ря(0x98);
	
	private int n;

	private EType(int n) {
		this.n = n;
	}
	
	public int getN() {
		return n;
	}
	
	public static EType getForInt(int typeId) {
		switch(typeId){
		case 1: 
			return EType.M_SP_NA;
		case 2: 
			return EType.M_SP_TA;
		case 3: 
			return EType.M_DP_NA;
		case 4: 
			return EType.M_DP_TA;
		case 5: 
			return EType.M_ST_NA;
		case 6: 
			return EType.M_ST_TA;
		case 7: 
			return EType.M_BO_NA;
		case 8: 
			return EType.M_BO_TA;
		case 9: 
			return EType.M_ME_NA;
		case 10: 
			return EType.M_ME_TA;
		case 11: 
			return EType.M_ME_NB;
		case 12: 
			return EType.M_ME_TB;
		case 13: 
			return EType.M_ME_NC;
		case 14: 
			return EType.л_ле_ря;
		case 15: 
			return EType.M_IT_NA;
		case 16: 
			return EType.M_IT_TA;
		case 17: 
			return EType.M_EP_TA;
		case 18: 
			return EType.л_еп_рб;
		case 19: 
			return EType.л_еп_ря;
		case 20: 
			return EType.M_PS_NA;
		case 21: 
			return EType.M_ME_ND;
		case 30: 
			return EType.M_SP_TB;
		case 31: 
			return EType.M_DP_TB;
		case 32: 
			return EType.M_ST_TB;
		case 33: 
			return EType.M_BO_TB;
		case 34: 
			return EType.M_ME_TD;
		case 35: 
			return EType.л_ле_ре;
		case 36: 
			return EType.M_ME_TF;
		case 37: 
			return EType.M_IT_TB;
		case 38: 
			return EType.M_EP_TD;
		case 39: 
			return EType.л_еп_ре;
		case 40: 
			return EType.M_EP_TF;
		case 41: 
			return EType.M_EP_TF;
		case 45: 
			return EType.C_SC_NA;
		case 46: 
			return EType.C_DC_NA;
		case 47: 
			return EType.C_RC_NA;
		case 48: 
			return EType.C_SE_NA;
		case 49: 
			return EType.C_SE_NB;
		case 50: 
			return EType.C_SE_NC;
		case 51: 
			return EType.C_BO_NA;
		case 70: 
			return EType.M_EI_NA;
		case 100: 
			return EType.C_IC_NA;
		case 101: 
			return EType.C_CI_NA;
		case 102: 
			return EType.C_RD_NA;
		case 103: 
			return EType.C_CS_NA;
		case 104: 
			return EType.C_TS_NA;
		case 105: 
			return EType.C_RP_NA;
		case 106: 
			return EType.C_CD_NA;
		case 110: 
			return EType.C_CD_NA;
		case 111: 
			return EType.P_ME_NB;
		case 112: 
			return EType.P_ME_NC;
		case 113: 
			return EType.P_AC_NA;
		case 120: 
			return EType.F_FR_NA;
		case 121: 
			return EType.F_SR_NA;
		case 122: 
			return EType.F_SC_NA;
		case 123: 
			return EType.F_LS_NA;
		case 124: 
			return EType.F_AF_NA;
		case 125: 
			return EType.F_SG_NA;
		case 126: 
			return EType.F_DR_TA;
		case 136: 
			return EType.M_BO_TC;
		case 139: 
			return EType.M_ME_NE;
		case 144: 
			return EType.M_ME_TI;
		case 145: 
			return EType.M_ME_TJ;
		case 150: 
			return EType.л_яб_рю;
		case 151: 
			return EType.л_яб_рб;
		case 152: 
			return EType.л_яб_ря;
			
		default:
			return null;
		}
	}
}
