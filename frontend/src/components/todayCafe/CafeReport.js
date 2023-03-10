import { useState } from "react"
import { Modal, Button, Form} from "semantic-ui-react"

import useFetch from "../../hooks/useFetch.js"
const DEFAULT_REST_URL = process.env.REACT_APP_REST_DEFAULT_URL

const CafeReport = (props) => {
  const cafeAuth = sessionStorage.getItem("cafeAuth")
  const [open, setOpen] = useState(false)

  const { sendRequest: newReply } = useFetch()
  
  const initialValues ={
    power: false,
    wifi: false,
    toilet: false,
    timeRestrict: null,
  }

  const [inputValues, setInputValues] = useState(initialValues)
  const {power, wifi, toilet, timeRestrict} = inputValues

  const handleChange = (selected) => {
    const {type, value} = selected
    setInputValues({...inputValues, [type]: value})
  }

  const submitHandler = async () => {
    if (power && wifi && toilet && timeRestrict !== null) {
      await newReply({
        url: `${DEFAULT_REST_URL}/todaycafe/main/survey`,
        method: "POST",
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`,
          'Content-Type': 'application/json'
        },
        body: {
          replyWifi : power,
          replyPower : wifi,
          replyToilet : toilet,
          replyTime : timeRestrict,
        },
      })
      props.setSurvey()
      alert('설문이 제출되었습니다!\uD83D\uDE0D')
      setOpen(false)
    } else {
      alert('모든 문항을 체크해주세요.\uD83D\uDE4F')
      console.log(power, wifi, toilet, timeRestrict)
    }
  }

  return (
    <Modal
      closeIcon
      onClose={() => setOpen(false)}
      onOpen={() => setOpen(true)}
      open={open}
      size="mini"
      trigger={<Button icon={props.icon} size={props.size} disabled={cafeAuth !== '1'} style={{float: 'right'}}>{props.content? props.content : null}</Button>}
    >
      <Modal.Header style={{display: 'flex', justifyContent: 'center'}}>카페 정보 제공하기</Modal.Header>
      <Modal.Content>
        <Form>
          <Form.Group inline>
            <label><p style={{width: '6rem'}}>콘센트</p></label>
            <Form.Radio label='충분' value='G' checked={power === 'G'} onChange={(e) => handleChange({type: 'power', value: 'G'})}/>
            <Form.Radio label='보통' value='N' checked={power === 'N'} onChange={(e) => handleChange({type: 'power', value: 'N'})}/>
            <Form.Radio label='부족' value='B' checked={power === 'B'} onChange={(e) => handleChange({type: 'power', value: 'B'})}/>
          </Form.Group>
          <Form.Group inline>
            <label><p style={{width: '6rem'}}>와이파이</p></label>
            <Form.Radio label='원활' value='G' checked={wifi === 'G'} onChange={(e) => handleChange({type: 'wifi', value: 'G'})}/>
            <Form.Radio label='보통' value='N' checked={wifi === 'N'} onChange={(e) => handleChange({type: 'wifi', value: 'N'})}/>
            <Form.Radio label='불안' value='B' checked={wifi === 'B'} onChange={(e) => handleChange({type: 'wifi', value: 'B'})}/>
          </Form.Group>
          <Form.Group inline>
            <label><p style={{width: '6rem'}}>화장실</p></label>
            <Form.Radio label='청결' value='G' checked={toilet === 'G'} onChange={(e) => handleChange({type: 'toilet', value: 'G'})}/>
            <Form.Radio label='보통' value='N' checked={toilet === 'N'} onChange={(e) => handleChange({type: 'toilet', value: 'N'})}/>
            <Form.Radio label='열악' value='B' checked={toilet === 'B'} onChange={(e) => handleChange({type: 'toilet', value: 'B'})}/>
          </Form.Group>
          <Form.Group inline>
            <label><p style={{width: '6rem'}}>카공 시간제한</p></label>
            <Form.Radio label='있음' value='YES' checked={timeRestrict === true} onChange={(e) => handleChange({type: 'timeRestrict', value: true})}/>
            <Form.Radio label='없음' value='NO' checked={timeRestrict === false} onChange={(e) => handleChange({type: 'timeRestrict', value: false})}/>
          </Form.Group>
          <Form.Group style={{display: 'flex', justifyContent: 'center'}}>
            <Form.Button onClick={submitHandler} color="blue">제출하기</Form.Button>
            <Form.Button onClick={() => setOpen(false)}>취소하기</Form.Button>
          </Form.Group>
        </Form>
      </Modal.Content>
    </Modal>
  )
}

export default CafeReport
