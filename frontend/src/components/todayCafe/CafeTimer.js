import { useEffect, useRef, Fragment } from "react"
import { useSelector, useDispatch } from "react-redux"
import { timerActions } from "../../store/timer"
import { Progress } from "semantic-ui-react"

const CafeTimer = () => {
  const dispatch = useDispatch()
  const accTime = useSelector((state) => state.timer.accTime)
  const addTimeHandler = () => {
    dispatch(timerActions.update(1))
  }

  const interval = useRef(null)

  useEffect(() => {
    // 1초(1000ms)마다 누적시간(accTime) 업데이트
    interval.current = setInterval(() => {
      addTimeHandler() // 리덕스에 저장된 accTime 값을 1초에 1씩 증가시키는 handler
    }, 1000)
    return () => clearInterval(interval.current)
  }, [])

  // 누적시간(accTime)이 변할 때만 실행되는 useEffect
  // 누적시간 2시간(= 7200초) 되면 interval을 멈춘다.
  useEffect(() => {
    if (accTime >= 7200) {
      clearInterval(interval.current)
    }
  }, [accTime])

  return (
    <Fragment>
      <Progress
        total={120}
        color="green"
        progress="value"
        value={parseInt(accTime / 60)}
      >
        <p style={{ color: "green" }}>
          {parseInt(accTime / 3600)}시간 {parseInt(accTime / 60) % 60}분 {accTime % 60}초 경과!
        </p>
      </Progress>
      {/* <section style={{ border: "dotted" }}>
        <h3>시간 확인용 정보.. (임시)</h3>
        <p>리덕스</p>
        <p>경과시간: {accTime}초</p>
      </section> */}
    </Fragment>
  )
}

export default CafeTimer