// 토큰 주고 받고, 유저 정보 저장을 위한 파일
import { useEffect } from "react"
import { useHistory } from "react-router-dom"
import CafeAuthFetch from "../../certificate/cafeAuth/CafeAuthFetch"

const DEFAULT_REST_URL = process.env.REACT_APP_REST_DEFAULT_URL

const KakaoLoginGetCode = ({setIsAuthenticated, setIsCafeAuth}) => {
  // 화면 생성시 시작
  const history = useHistory()
  const PARAMS = new URL(document.location).searchParams
  const KAKAO_CODE = PARAMS.get("code")
  useEffect(() => {
    
    const startLogin = async () => {
      try {
        // 토큰 가져오기(백엔드에서 토큰을 주는 url 넣어야함)
        const response = await fetch(`${DEFAULT_REST_URL}/oauth/kakao?code=${KAKAO_CODE}`, {
          method: "GET",
        })
        // DB 저장되어 있는 유저면
        const responseData = await response.json()
        if (responseData.httpStatus === "OK") {
          sessionStorage.setItem("accessToken", responseData.data.jwtTokens.accessToken)
          sessionStorage.setItem("refreshToken", responseData.data.jwtTokens.refreshToken)
          sessionStorage.setItem("nickname", responseData.data.nickname)
          setIsAuthenticated(true)
          history.push("/")
        }
        // 첫 로그인 회원일 경우
        else if (responseData.httpStatus === "CREATED") {
          history.push({
            pathname: `/signup`,
            state: { oauthId: responseData.data.kakaoMemberId, oauthType: responseData.data.oauthType },
          })
        }
        else {
          throw new Error('에러')
        }
      } catch (error) {
        window.location.href ="/login"
      }
    }
    startLogin()
  }, [history, KAKAO_CODE])
}

export default KakaoLoginGetCode
