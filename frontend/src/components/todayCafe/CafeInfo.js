import { useState, useEffect } from "react"
import { useDispatch } from "react-redux"
import { Grid, Container, Button } from "semantic-ui-react"
import { BsFillPatchQuestionFill } from "react-icons/bs"
import { modalActions } from "../../store/modal"
import CafeReport from "./CafeReport"
import CafeTimer from "./CafeTimer"
import useFetch from "../../hooks/useFetch"
const DEFAULT_REST_URL = process.env.REACT_APP_REST_DEFAULT_URL

const BRAND_LOGOS = {
  할리스: "hollys",
  폴바셋: "paulbasset",
  파스쿠찌: "pascucci",
  투썸플레이스: "twosome",
  토프레소: "topresso",
  텐퍼센트커피: "tenpercent",
  탐앤탐스: "tomntoms",
  컴포즈커피: "compose",
  커피에반하다: "coffeebanada",
  커피스미스: "coffeesmith",
  커피빈: "coffeebean",
  커피베이: "coffeebay",
  커피나무: "coffeenamu",
  카페베네: "caffeebene",
  카페띠아모: "caffetiamo",
  전광수: "jungwang",
  이디야커피: "edia",
  요거프레소: "yogerpresso",
  엔제리너스: "angelinus",
  스타벅스: "starbucks",
  스무디킹: "smoothy",
  셀렉토커피: "selecto",
  빽다방: "paiksdabang",
  베스킨라빈스: "baskin",
  메가커피: "megacoffee",
  매머드: "mammoth",
  드롭탑: "droptop",
  더벤티: "theventi",
  달콤커피: "dalkomm",
  나우커피: "nowcoffee",
  공차: "gongcha",
  개인카페: "selfcafe",
  바나프레소: "banapresso",
}
const fullDate = (date) => {
  const yyyy = date.getFullYear()
  const mm = date.getMonth() + 1
  const dd = date.getDate()
  return yyyy * 10000 + mm * 100 + dd
}

const CafeInfo = () => {
  const dispatch = useDispatch()
  const cafeAuth = sessionStorage.getItem("cafeAuth")
  const nowCafe = JSON.parse(sessionStorage.getItem("myCafe"))
  const todayCafe = JSON.parse(sessionStorage.getItem("todayCafe"))
  let cafeName = ""
  let logo_url = ""
  let tierColor = ""

  if (nowCafe === null || todayCafe === null) {
    cafeName = "카페가 인증되지 않았습니다."
    logo_url = "selfcafe.png"
  } else {
    cafeName = nowCafe.cafeName
    logo_url = `${BRAND_LOGOS[todayCafe.brandType]}.png`
    tierColor =
      parseInt(todayCafe.exp / 1000) < 5
        ? ["#8B6331", "#C0C0C0", "#FF9614", "#3DFF92"][
            parseInt(todayCafe.exp / 1000)
          ]
        : "#65B1EF"
  }

  const [isSurveySubmitted, setIsSurveySubmitted] = useState(false)
  const { data: fetchedData, sendRequest: getIsSurveySubmitted } = useFetch()
  useEffect(() => {
    if (cafeAuth === "1") {
      getIsSurveySubmitted({
        url: `${DEFAULT_REST_URL}/todaycafe/main/survey/check?todayDate=${fullDate(
          new Date()
        )}`,
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`,
        },
      })
    }
  }, [])

  useEffect(() => {
    setIsSurveySubmitted(fetchedData.isSurveySubmitted)
  }, [fetchedData])

  const changeSubmittedState = () => {
    setIsSurveySubmitted(true)
  }

  return (
    <Container style={{ backgroundColor: "#faf6ee" }}>
      <Grid>
        {/* 모바일 태블릿 화면 카페 정보 */}
        <Grid.Row only="mobile tablet">
          <Grid.Column mobile={3} tablet={5} computer={1} />
          <Grid.Column mobile={10} tablet={6} computer={5}>
            {(cafeAuth === "0" || cafeAuth === null) && (
              <BsFillPatchQuestionFill
                style={{ marginInline: "0.5rem 0.8rem" }}
                size="100%"
                color="grey"
              />
            )}
            {cafeAuth === "1" && (
              <img
                src={require(`../../assets/cafe_logos/${logo_url}`)}
                style={{
                  border: `3vw solid ${tierColor}`,
                  borderRadius: "70%",
                }}
                alt="#"
              />
            )}
          </Grid.Column>
          <Grid.Column only="tablet computer" tablet={5} computer={10}>
            <Grid style={{ textAlign: "center" }}>
              <Grid.Row style={{ display: "flex", justifyContent: "center" }}>
                {cafeAuth === "1" && !isSurveySubmitted && (
                  <CafeReport
                    icon={false}
                    size={"large"}
                    content={"제보하기"}
                    setSurvey={changeSubmittedState}
                  />
                )}
                {(cafeAuth === "0" || cafeAuth === null) && (
                  <Button
                    onClick={() => {
                      dispatch(modalActions.openCafeAuthModal())
                    }}
                  >
                    위치 인증
                  </Button>
                )}
              </Grid.Row>
            </Grid>
          </Grid.Column>
          <Grid.Column only="mobile" mobile={3}>
            {cafeAuth === "1" && !isSurveySubmitted && (
              <CafeReport
                icon={"write square"}
                size={"mini"}
                content={null}
                setSurvey={changeSubmittedState}
              />
            )}
            {(cafeAuth === "0" || cafeAuth === null) && (
              <Button
                onClick={() => {
                  dispatch(modalActions.openCafeAuthModal())
                }}
              >
                위치 인증
              </Button>
            )}
          </Grid.Column>
        </Grid.Row>

        {/* 데스크탑 화면 카페 정보 - 여기에 시간 바 들어감 */}
        <Grid.Row style={{ width: "100%" }} centered>
          <Grid columns={2}>
            <Grid.Column only="computer" computer={5}>
              {cafeAuth === "0" && (
                <BsFillPatchQuestionFill
                  style={{ marginInline: "0.5rem 0.8rem" }}
                  size="100%"
                  color="grey"
                />
              )}
              {cafeAuth === "1" && (
                <img
                  src={require(`../../assets/cafe_logos/${logo_url}`)}
                  style={{
                    width: "100%",
                    border: `1vw solid ${tierColor}`,
                    borderRadius: "70%",
                  }}
                  alt="#"
                />
              )}
            </Grid.Column>
            <Grid.Column
              mobile={16}
              tablet={16}
              computer={11}
              style={{ height: "100%" }}
            >
              <Grid style={{ display: "flex", alignItems: "center" }}>
                <Grid.Row columns={2}>
                  <Grid.Column
                    mobile={16}
                    tablet={16}
                    computer={11}
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    <p
                      style={{
                        fontSize: "large",
                        textAlign: "center",
                        color: "#1E3932",
                        fontSize: "220%",
                        fontFamily: "GangwonEdu_OTFBoldA",
                        wordBreak: "keep-all",
                      }}
                    >
                      {cafeName}
                    </p>
                  </Grid.Column>
                  <Grid.Column only="computer" computer={5}>
                    {cafeAuth === "1" && !isSurveySubmitted && (
                      <CafeReport
                        icon={false}
                        size={"large"}
                        content={"제보하기"}
                        setSurvey={changeSubmittedState}
                      />
                    )}
                    {(cafeAuth === "0" || cafeAuth === null) && (
                      <Button
                        onClick={() => {
                          dispatch(modalActions.openCafeAuthModal())
                        }}
                      >
                        위치 인증
                      </Button>
                    )}
                  </Grid.Column>
                </Grid.Row>
                <Grid.Row columns={1}>
                  <Grid.Column>
                    <CafeTimer />
                  </Grid.Column>
                </Grid.Row>
              </Grid>
            </Grid.Column>
          </Grid>
        </Grid.Row>
      </Grid>
    </Container>
  )
}

export default CafeInfo
